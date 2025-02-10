package com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.crop_image

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.ImageEditViewModel
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components.Corner
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components.drawHandle
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components.getCroppedBitmap
import com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components.isNear

@Composable
fun CropImageScreen(
    navController: NavController,
    imageEditViewModel: ImageEditViewModel
) {
    val imageState = imageEditViewModel.bitmapState.collectAsState()


    var draggingCorner by remember { mutableStateOf<Corner?>(null) }
    var draggingCenter by remember { mutableStateOf(false) }

    val bitmapState = imageEditViewModel.bitmapState.collectAsState()
    val matrixState = imageEditViewModel.matrix.collectAsState()

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val topLeft = imageEditViewModel.topLeft.collectAsState()
    val topRight = imageEditViewModel.topRight.collectAsState()
    val bottomLeft = imageEditViewModel.bottomLeft.collectAsState()
    val bottomRight = imageEditViewModel.bottomRight.collectAsState()

    val offsetChanged = imageEditViewModel.offsetChanged.collectAsState()

    val screenWidthFlow = imageEditViewModel.screenWidth.collectAsState()
    val screenHeightFlow = imageEditViewModel.screenHeight.collectAsState()


    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .weight(9f)
        ) {
            val screenWidth = constraints.maxWidth.toFloat()
            val screenHeight = constraints.maxHeight.toFloat()

            val rectWidth = screenWidth * 0.6f  // 60% of screen width
            val rectHeight = screenHeight * 0.4f  // 40% of screen height

            LaunchedEffect(true) {
                imageEditViewModel.updateScreenSize(
                    width = constraints.maxWidth.toFloat(),
                    height = constraints.maxHeight.toFloat()
                )
                Log.d("adskjlasd", "${screenWidth} ${screenHeight}")
                if (!offsetChanged.value) {
                    imageEditViewModel.updateOffsets(
                        newTopLeft = Offset(
                            (screenWidth - rectWidth) / 2,
                            (screenHeight - rectHeight) / 2
                        ),
                        newTopRight = Offset(
                            (screenWidth + rectWidth) / 2,
                            (screenHeight - rectHeight) / 2
                        ),
                        newBottomLeft = Offset(
                            (screenWidth - rectWidth) / 2,
                            (screenHeight + rectHeight) / 2
                        ),
                        newBottomRight = Offset(
                            (screenWidth + rectWidth) / 2,
                            (screenHeight + rectHeight) / 2
                        )
                    )

                }
            }

            imageState.value.initialBitmap?.let { image ->
                val animatedRotation by animateFloatAsState(
                    targetValue = bitmapState.value.rotation,
                    animationSpec = tween(durationMillis = 300) // Adjust speed if needed
                )

                val paint = remember(matrixState.value) {
                    Paint().apply {
                        colorFilter = ColorFilter.colorMatrix(ColorMatrix(matrixState.value))
                    }
                }

                val screenWidthCurrent = LocalConfiguration.current.screenWidthDp.dp
                val screenHeightCurrent = LocalConfiguration.current.screenHeightDp.dp

                val isPortrait by derivedStateOf { animatedRotation % 180 == 0f } // Detect if upright

                val animatedSize by animateDpAsState(
                    targetValue = if (isPortrait) screenHeightCurrent else min(screenWidthCurrent, screenHeightCurrent),
                    animationSpec = tween(durationMillis = 300), label = "" // Smooth animation
                )

                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .rotate(animatedRotation)
                        .size(animatedSize)
                        .align(Alignment.Center)
                        .drawWithContent {
                            drawIntoCanvas { canvas ->
                                canvas.saveLayer(size.toRect(), paint)
                                drawContent()
                                canvas.restore()
                            }
                        }, // Smooth scaling
                    contentScale = ContentScale.Inside
                )

                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset: Offset ->
                                draggingCorner = when {
                                    offset.isNear(topLeft.value) -> Corner.TopLeft
                                    offset.isNear(topRight.value) -> Corner.TopRight
                                    offset.isNear(bottomLeft.value) -> Corner.BottomLeft
                                    offset.isNear(bottomRight.value) -> Corner.BottomRight
                                    else -> null
                                }

                                draggingCenter = draggingCorner == null && Rect(
                                    topLeft.value,
                                    bottomRight.value
                                ).contains(offset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                when (draggingCorner) {
                                    Corner.TopLeft -> {
                                        imageEditViewModel.updateOffsets(
                                            newTopLeft = topLeft.value + dragAmount,
                                            newTopRight = topRight.value.copy(y = topLeft.value.y),
                                            newBottomLeft = bottomLeft.value.copy(x = topLeft.value.x),
                                            newBottomRight = bottomRight.value
                                        )
                                    }

                                    Corner.TopRight -> {
                                        imageEditViewModel.updateOffsets(
                                            newTopLeft = topLeft.value.copy(y = topRight.value.y),
                                            newTopRight = topRight.value + dragAmount,
                                            newBottomLeft = bottomLeft.value,
                                            newBottomRight = bottomRight.value.copy(x = topRight.value.x)
                                        )
                                    }

                                    Corner.BottomLeft -> {
                                        imageEditViewModel.updateOffsets(
                                            newTopLeft = topLeft.value.copy(x = bottomLeft.value.x),
                                            newTopRight = topRight.value,
                                            newBottomLeft = bottomLeft.value + dragAmount,
                                            newBottomRight = bottomRight.value.copy(y = bottomLeft.value.y)
                                        )
                                    }

                                    Corner.BottomRight -> {
                                        imageEditViewModel.updateOffsets(
                                            newTopLeft = topLeft.value,
                                            newTopRight = topRight.value.copy(x = bottomRight.value.x),
                                            newBottomLeft = bottomLeft.value.copy(y = bottomRight.value.y),
                                            newBottomRight = bottomRight.value + dragAmount
                                        )
                                    }

                                    null -> if (draggingCenter) {
                                        imageEditViewModel.updateOffsets(
                                            newTopLeft = topLeft.value + dragAmount,
                                            newTopRight = topRight.value + dragAmount,
                                            newBottomLeft = bottomLeft.value + dragAmount,
                                            newBottomRight = bottomRight.value + dragAmount
                                        )
                                    }

                                }
                            },
                            onDragEnd = {
                                imageEditViewModel.updateOffsets(
                                    topLeft.value,
                                    topRight.value,
                                    bottomLeft.value,
                                    bottomRight.value
                                )
                                draggingCenter = false
                                draggingCorner = null
                            }
                        )
                    }) {


                    drawIntoCanvas { canvas ->
                        // Create a new layer for proper blending
                        val layerPaint = Paint()
                        canvas.saveLayer(size.toRect(), layerPaint)

                        // Draw a dark overlay
                        drawRect(
                            color = Color.Black.copy(alpha = 0.6f), // Semi-transparent black overlay
                            size = size
                        )

                        // Use DstOut mode to make the inner rectangle fully transparent
                        val cutoutPaint = Paint().apply {
                            blendMode = BlendMode.DstOut
                        }
                        canvas.drawRect(
                            Rect(topLeft.value, bottomRight.value),
                            cutoutPaint
                        )

                        canvas.restore()
                    }
                    drawHandle(topLeft.value)
                    drawHandle(topRight.value)
                    drawHandle(bottomLeft.value)
                    drawHandle(bottomRight.value)
                }

            }



        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(modifier = Modifier.size(24.dp), imageVector = Icons.Default.Close, contentDescription = "")
            }
            IconButton(onClick = {
                imageEditViewModel.rotateBitmap()
            }) {
                Icon(modifier = Modifier.size(24.dp),painter = painterResource(R.drawable.rotate_ic), contentDescription = "")
            }
            IconButton(onClick = {
                bitmapState.value.rotatedBitmap?.let {
                    val croppedBitmap = getCroppedBitmap(
                        it.asImageBitmap(),
                        Rect(topLeft.value, bottomRight.value),
                        canvasWidth = screenWidthFlow.value,
                        canvasHeight = screenHeightFlow.value
                    )

                    val matrix = Matrix().apply { postRotate(-bitmapState.value.rotation) }
                    val rotatedBitmap = Bitmap.createBitmap(
                        croppedBitmap,
                        0,
                        0,
                        croppedBitmap.width,
                        croppedBitmap.height,
                        matrix,
                        true
                    )

                    imageEditViewModel.cropImage(rotatedBitmap) {
                        navController.popBackStack()
                    }
                }

            }) {
                Icon(modifier = Modifier.size(24.dp), imageVector = Icons.Default.Done, contentDescription = "")
            }
        }


    }
}