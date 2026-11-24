package com.zaurh.bober.screen.image_edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Photo filters. [matrix] is a 4x5 color matrix (same layout for Android and Compose). */
enum class PhotoFilter(val label: String, val matrix: FloatArray?) {
    ORIGINAL("Original", null),
    VIVID("Vivid", saturation(1.45f)),
    WARM("Warm", floatArrayOf(
        1.10f, 0f, 0f, 0f, 12f,
        0f, 1.02f, 0f, 0f, 4f,
        0f, 0f, 0.88f, 0f, -8f,
        0f, 0f, 0f, 1f, 0f,
    )),
    COOL("Cool", floatArrayOf(
        0.90f, 0f, 0f, 0f, -6f,
        0f, 1.0f, 0f, 0f, 0f,
        0f, 0f, 1.12f, 0f, 14f,
        0f, 0f, 0f, 1f, 0f,
    )),
    CONTRAST("Contrast", contrast(1.35f)),
    BRIGHT("Bright", floatArrayOf(
        1f, 0f, 0f, 0f, 30f,
        0f, 1f, 0f, 0f, 30f,
        0f, 0f, 1f, 0f, 30f,
        0f, 0f, 0f, 1f, 0f,
    )),
    LOMO("Lomo", floatArrayOf(
        1.45f, 0f, 0f, 0f, -45f,
        0f, 1.35f, 0f, 0f, -40f,
        0f, 0f, 1.10f, 0f, -20f,
        0f, 0f, 0f, 1f, 0f,
    )),
    SEPIA("Sepia", floatArrayOf(
        0.393f, 0.769f, 0.189f, 0f, 0f,
        0.349f, 0.686f, 0.168f, 0f, 0f,
        0.272f, 0.534f, 0.131f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )),
    MONO("B&W", saturation(0f)),
    INVERT("Invert", floatArrayOf(
        -1f, 0f, 0f, 0f, 255f,
        0f, -1f, 0f, 0f, 255f,
        0f, 0f, -1f, 0f, 255f,
        0f, 0f, 0f, 1f, 0f,
    )),
}

private fun saturation(s: Float): FloatArray = android.graphics.ColorMatrix().apply { setSaturation(s) }.array

private fun contrast(c: Float): FloatArray {
    val t = (1f - c) * 128f
    return floatArrayOf(
        c, 0f, 0f, 0f, t,
        0f, c, 0f, 0f, t,
        0f, 0f, c, 0f, t,
        0f, 0f, 0f, 1f, 0f,
    )
}

/** Decodes [uri] upright (EXIF orientation applied) and no larger than [maxSide] px. Blocking. */
fun loadBitmap(context: Context, uri: Uri, maxSide: Int = 2048): Bitmap? = runCatching {
    val resolver = context.contentResolver
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // ImageDecoder applies the EXIF rotation itself.
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri)) { decoder, info, _ ->
            val largest = max(info.size.width, info.size.height)
            if (largest > maxSide) {
                val ratio = maxSide.toFloat() / largest
                decoder.setTargetSize((info.size.width * ratio).roundToInt(), (info.size.height * ratio).roundToInt())
            }
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE // Canvas/createBitmap need a software bitmap
        }
    } else {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        val decoded = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: return null
        val degrees = resolver.openInputStream(uri)?.use { exifRotation(ExifInterface(it)) } ?: 0
        if (degrees == 0) decoded else rotate(decoded, degrees)
    }
}.getOrNull()

private fun exifRotation(exif: ExifInterface): Int = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
    ExifInterface.ORIENTATION_ROTATE_90 -> 90
    ExifInterface.ORIENTATION_ROTATE_180 -> 180
    ExifInterface.ORIENTATION_ROTATE_270 -> 270
    else -> 0
}

fun rotate(bitmap: Bitmap, degrees: Int): Bitmap =
    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(degrees.toFloat()) }, true)

/** A small copy for filter thumbnails. */
fun thumbnail(bitmap: Bitmap, side: Int = 160): Bitmap {
    val ratio = side.toFloat() / min(bitmap.width, bitmap.height)
    return Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).roundToInt().coerceAtLeast(1), (bitmap.height * ratio).roundToInt().coerceAtLeast(1), true)
}

/**
 * Crops [bitmap] to the region visible in the editor's frame, applies [filter], and writes a JPEG to the
 * cache. Returns its Uri (readable with ContentResolver like a picked image). Blocking.
 *
 * @param crop the visible region as fractions of the bitmap: left, top, width, height in 0..1.
 */
fun exportEdited(context: Context, bitmap: Bitmap, crop: CropFraction, filter: PhotoFilter, maxWidth: Int = 1080): Uri {
    val x = (crop.left * bitmap.width).roundToInt().coerceIn(0, bitmap.width - 1)
    val y = (crop.top * bitmap.height).roundToInt().coerceIn(0, bitmap.height - 1)
    val w = (crop.width * bitmap.width).roundToInt().coerceIn(1, bitmap.width - x)
    val h = (crop.height * bitmap.height).roundToInt().coerceIn(1, bitmap.height - y)
    val scale = min(1f, maxWidth.toFloat() / w)
    val outW = (w * scale).roundToInt().coerceAtLeast(1)
    val outH = (h * scale).roundToInt().coerceAtLeast(1)

    val output = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
    val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
        filter.matrix?.let { colorFilter = ColorMatrixColorFilter(it) }
    }
    Canvas(output).drawBitmap(bitmap, android.graphics.Rect(x, y, x + w, y + h), android.graphics.Rect(0, 0, outW, outH), paint)

    val dir = File(context.cacheDir, "edited").apply { mkdirs() }
    // Old edits are only needed until they're uploaded.
    dir.listFiles()?.filter { System.currentTimeMillis() - it.lastModified() > 60 * 60 * 1000 }?.forEach { it.delete() }
    val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { output.compress(Bitmap.CompressFormat.JPEG, 90, it) }
    output.recycle()
    return Uri.fromFile(file)
}

data class CropFraction(val left: Float, val top: Float, val width: Float, val height: Float)
