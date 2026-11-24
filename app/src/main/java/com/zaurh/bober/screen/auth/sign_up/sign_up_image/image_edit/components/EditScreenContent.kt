package com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.components

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.BRIGHTNESS
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.CONTRAST
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.COOL
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.FilterType
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.GRAYSCALE
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.INVERT
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.LOMO
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.ORIGINAL
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.SEPIA
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.components.WARM
import com.zaurh.bober.screen.auth.sign_up.sign_up_image.image_edit.ImageEditViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import java.io.OutputStream
import kotlin.math.min
import kotlin.math.roundToInt

enum class Corner {
    TopLeft, TopRight, BottomLeft, BottomRight
}

@Composable
fun EditScreenContent(
    modifier: Modifier = Modifier,
    imageEditViewModel: ImageEditViewModel
) {

    val context = LocalContext.current

    val bitmapState = imageEditViewModel.bitmapState.collectAsState()
    val filterState = imageEditViewModel.filterState.collectAsState()
    val matrixState = imageEditViewModel.matrix.collectAsState()

    LaunchedEffect(true) {
        val rotation = bitmapState.value.rotation
        Log.d("dskaljdslka", "$rotation")
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Column(
//            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }


            val paint = remember(matrixState.value) {
                Paint().apply {
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix(matrixState.value))
                }
            }
            bitmapState.value.croppedBitmap?.let { image ->
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .weight(8f)
                ) {

                    val animatedRotation by animateFloatAsState(
                        targetValue = bitmapState.value.rotation,
                        animationSpec = tween(durationMillis = 300) // Adjust speed if needed
                    )

                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = "Image to crop",
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(
                                animatedRotation
                            )
                            .drawWithContent {
                                drawIntoCanvas { canvas ->
                                    canvas.saveLayer(size.toRect(), paint)
                                    drawContent()
                                    canvas.restore()
                                }
                            }
                            .align(Alignment.Center)
                            .pointerInput(Unit) {
                                detectTransformGestures(onGesture = { _, pan, zoom, _ ->
                                    val newScale = scale * zoom
                                    scale = newScale.coerceIn(0.5f, 2f)

                                    // Apply the pan, but clamp the offset to stay within bounds
                                    offset += pan

                                })
                                detectTapGestures(onDoubleTap = {
                                    scale = 1f; offset = Offset.Zero
                                })
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }


            Row(
                Modifier
//                    .fillMaxHeight()
                    .weight(2f)
                    .background(MaterialTheme.colorScheme.surface)
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bitmapState.value.croppedBitmap?.let {
                    FilterImages(
                        bitmap = it,
                        floatArray = ORIGINAL,
                        rotation = bitmapState.value.rotation,
                        filterName = "Original"
                    ) {
                        imageEditViewModel.applyFilter(FilterType.ORIGINAL)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = GRAYSCALE,
                        rotation = bitmapState.value.rotation,
                        filterName = "Grayscale"

                    ) {
                        imageEditViewModel.applyFilter(FilterType.GRAYSCALE)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = SEPIA,
                        rotation = bitmapState.value.rotation,
                        filterName = "Sepia"
                    ) {
                        imageEditViewModel.applyFilter(FilterType.SEPIA)
                    }

                    FilterImages(
                        bitmap = it,
                        floatArray = INVERT,
                        rotation = bitmapState.value.rotation,
                        filterName = "Invert"
                    ) {
                        imageEditViewModel.applyFilter(FilterType.INVERT)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = BRIGHTNESS,
                        rotation = bitmapState.value.rotation,
                        filterName = "Brightness"
                    ) {
                        imageEditViewModel.applyFilter(FilterType.BRIGHTNESS)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = CONTRAST,
                        rotation = bitmapState.value.rotation,
                        filterName = "Contrast"

                    ) {
                        imageEditViewModel.applyFilter(FilterType.CONTRAST)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = LOMO,
                        rotation = bitmapState.value.rotation,
                        filterName = "Lomo"
                    ) {
                        imageEditViewModel.applyFilter(FilterType.LOMO)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = COOL,
                        rotation = bitmapState.value.rotation,
                        filterName = "Cool"

                    ) {
                        imageEditViewModel.applyFilter(FilterType.COOL)
                    }
                    FilterImages(
                        bitmap = it,
                        floatArray = WARM,
                        rotation = bitmapState.value.rotation,
                        filterName = "Warm"


                    ) {
                        imageEditViewModel.applyFilter(FilterType.WARM)
                    }
                }


            }


        }
//        Button(onClick = {
//            bitmapState.filteredBitmap?.let {
//                val croppedBitmap = getCroppedBitmap(
//                    it.asImageBitmap(),
//                    Rect(topLeft, bottomRight),
//                    canvasWidth = constraints.maxWidth.toFloat(),
//                    canvasHeight = constraints.maxHeight.toFloat()
//                )
//
//
//                //set the cropped image to the composable
////                image = croppedBitmap.asImageBitmap()
//            }
//
//        }) {
//            Text("sadfkldsjlsd")
//        }
    }


}


@Composable
fun FilterImages(
    filterName: String,
    bitmap: Bitmap,
    rotation: Float,
    floatArray: FloatArray,
    onClick: () -> Unit
) {
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 300) // Adjust speed if needed
    )
    val paint = remember() {
        Paint().apply {
            colorFilter = ColorFilter.colorMatrix(ColorMatrix(floatArray))
        }
    }
    Box(
        Modifier
            .widthIn(50.dp, 100.dp)
            .fillMaxHeight()) {
        Image(
            painter = BitmapPainter(bitmap.asImageBitmap()),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(10.dp))
                .rotate(animatedRotation)
                .clickable {
                    onClick()
                }
                .drawWithContent {
                    drawIntoCanvas { canvas ->
                        canvas.saveLayer(size.toRect(), paint)
                        drawContent()
                        canvas.restore()
                    }
                },
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .heightIn(20.dp, 200.dp)
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(10.dp))
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Text(
                text = filterName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = outfitFamily
            )
        }

    }
}


fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, title)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            "Pictures/YourAppName"
        ) // Save to "Pictures" folder
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { imageUri ->
        try {
            val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            outputStream?.flush()
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Offset.isNear(point: Offset, threshold: Float = 80f): Boolean {
    return (this - point).getDistance() <= threshold
}

fun DrawScope.drawHandle(center: Offset) {
    drawCircle(
        color = Color.White,
        radius = 20f,
        center = center
    )
}

fun getCroppedBitmap(
    imageBitmap: ImageBitmap,   // The original ImageBitmap
    cropRect: Rect,             // The crop rectangle area on the canvas
    canvasWidth: Float,         // The width of the canvas where the image is displayed
    canvasHeight: Float         // The height of the canvas where the image is displayed
): Bitmap {
    val bitmapWidth = imageBitmap.width.toFloat()
    val bitmapHeight = imageBitmap.height.toFloat()

    // Calculate scaling factors to fit the image within the canvas
    val widthRatio = canvasWidth / bitmapWidth
    val heightRatio = canvasHeight / bitmapHeight

    val scaleFactor = min(widthRatio, heightRatio) // Preserve aspect ratio

    // Calculate the actual displayed image dimensions within the canvas
    val displayedImageWidth = bitmapWidth * scaleFactor
    val displayedImageHeight = bitmapHeight * scaleFactor

    // Calculate the offset to center the image within the canvas
    val offsetX = (canvasWidth - displayedImageWidth) / 2
    val offsetY = (canvasHeight - displayedImageHeight) / 2

    // Map the crop rectangle coordinates from the canvas to the original image dimensions
    val cropLeft =
        ((cropRect.left - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
    val cropTop =
        ((cropRect.top - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapHeight.toInt())
    val cropRight =
        ((cropRect.right - offsetX) / scaleFactor).roundToInt().coerceIn(0, bitmapWidth.toInt())
    val cropBottom =
        ((cropRect.bottom - offsetY) / scaleFactor).roundToInt().coerceIn(0, bitmapHeight.toInt())

    // Calculate the cropped area width and height
    val cropWidth = (cropRight - cropLeft).coerceAtLeast(1)  // Ensure minimum 1px width
    val cropHeight = (cropBottom - cropTop).coerceAtLeast(1)  // Ensure minimum 1px height

    // Create a cropped bitmap from the original bitmap using the calculated rectangle
    return Bitmap.createBitmap(
        imageBitmap.asAndroidBitmap(),
        cropLeft,
        cropTop,
        cropWidth,
        cropHeight
    )
}