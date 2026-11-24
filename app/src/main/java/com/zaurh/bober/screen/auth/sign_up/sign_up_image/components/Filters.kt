package com.zaurh.bober.screen.auth.sign_up.sign_up_image.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

sealed class FilterType {
    data object ORIGINAL: FilterType()
    data object GRAYSCALE : FilterType()
    data object SEPIA : FilterType()
    data object INVERT : FilterType()
    data object BRIGHTNESS : FilterType()
    data object CONTRAST : FilterType()
    data object LOMO: FilterType()
    data object COOL: FilterType()
    data object WARM: FilterType()
}

fun applyHueFilter(hue: Float, bitmap: Bitmap): Bitmap {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)  // Convert to grayscale
    val rotateMatrix = ColorMatrix()
    rotateMatrix.setRotate(0, hue) // Rotate Red channel
    rotateMatrix.setRotate(1, hue) // Rotate Green channel
    rotateMatrix.setRotate(2, hue) // Rotate Blue channel

    colorMatrix.postConcat(rotateMatrix)  // Apply the hue rotation

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val hueBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(hueBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return hueBitmap
}

fun applySaturationFilter(saturation: Float, bitmap: Bitmap): Bitmap {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(saturation)

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val saturatedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(saturatedBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return saturatedBitmap

}

fun applyContrastFilter(contrast: Float, bitmap: Bitmap): Bitmap {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)  // Convert to grayscale first
    colorMatrix.setScale(contrast, contrast, contrast, 1f)  // Adjust contrast

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val contrastBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(contrastBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return contrastBitmap

}

fun applyBrightnessFilter(brightness: Float, bitmap: Bitmap): Bitmap {
    val colorMatrix = ColorMatrix()
    colorMatrix.setScale(brightness, brightness, brightness, 1f)  // Apply brightness scaling

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val brightnessBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(brightnessBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return brightnessBitmap

}


fun applyInvertFilter(bitmap: Bitmap): Bitmap {
    // Create a color matrix to invert colors
    val colorMatrix = ColorMatrix()
    // Invert the RGB values
    colorMatrix.set(INVERT)

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val invertedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(invertedBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return invertedBitmap
}


fun applySepiaFilter(bitmap: Bitmap): Bitmap {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)  // Make it grayscale

    colorMatrix.postConcat(ColorMatrix(SEPIA))  // Add the sepia transformation

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val sepiaBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(sepiaBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return sepiaBitmap

}

fun applyBlackAndWhite(bitmap: Bitmap): Bitmap{
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f) // Set saturation to 0 to make it grayscale

    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    val blackAndWhiteBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(blackAndWhiteBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return blackAndWhiteBitmap

}

fun bitmapToUriString(context: Context, bitmap: Bitmap): String? {
    // Create a temporary file to store the bitmap
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(imagesDir, "image_${System.currentTimeMillis()}.png")

    try {
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Convert the file to a URI string
        return file.toUri().toString() // Convert URI to String
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}

val ORIGINAL = floatArrayOf(
    1f, 0f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f, 0f,
    0f, 0f, 1f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)

val GRAYSCALE = floatArrayOf(
    0.3f, 0.3f, 0.3f, 0f, 0f,
    0.3f, 0.3f, 0.3f, 0f, 0f,
    0.3f, 0.3f, 0.3f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)


val SEPIA = floatArrayOf(
    0.393f, 0.769f, 0.189f, 0f, 0f,
    0.349f, 0.686f, 0.168f, 0f, 0f,
    0.272f, 0.534f, 0.131f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)


val INVERT = floatArrayOf(
    -1f, 0f, 0f, 0f, 255f,  // Invert red
    0f, -1f, 0f, 0f, 255f,  // Invert green
    0f, 0f, -1f, 0f, 255f,  // Invert blue
    0f, 0f, 0f, 1f, 0f      // Keep alpha unchanged
)

val BRIGHTNESS = floatArrayOf(
    1.2f, 0f, 0f, 0f, 0.5f,
    0f, 1.2f, 0f, 0f, 0.5f,
    0f, 0f, 1.2f, 0f, 0.5f,
    0f, 0f, 0f, 1f, 0f
)



val CONTRAST = floatArrayOf(
    1.5f, 0f, 0f, 0f, -0.5f,
    0f, 1.5f, 0f, 0f, -0.5f,
    0f, 0f, 1.5f, 0f, -0.5f,
    0f, 0f, 0f, 1f, 0f
)

val WARM = floatArrayOf(
    1.2f, 0.2f, 0f, 0f, 0f,
    0.1f, 1f, 0f, 0f, 0f,
    0f, 0.1f, 1.2f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)

val COOL = floatArrayOf(
    0.6f, 0.2f, 0.2f, 0f, 0f,
    0.2f, 0.6f, 0.2f, 0f, 0f,
    0.2f, 0.2f, 0.6f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)

val LOMO = floatArrayOf(
    1.2f, 0.2f, 0.2f, 0f, 0f,
    0.1f, 1f, 0f, 0f, 0f,
    0.2f, 0.1f, 1.5f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f
)
