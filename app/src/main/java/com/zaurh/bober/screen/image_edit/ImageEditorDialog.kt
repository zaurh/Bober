package com.zaurh.bober.screen.image_edit

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

/** Profile photos are portrait cards (see PersonCard / the swipe deck). */
private const val FRAME_ASPECT = 3f / 4f
private const val MAX_ZOOM = 5f

private fun PhotoFilter.colorFilter(): ColorFilter? = matrix?.let { ColorFilter.colorMatrix(ColorMatrix(it)) }

/**
 * Full-screen editor shown after picking a photo: drag/pinch to frame it in a 3:4 crop, rotate,
 * and pick a filter. [onDone] gets a Uri to the edited JPEG; [onCancel] keeps nothing.
 */
@Composable
fun ImageEditorDialog(source: Uri, onDone: (Uri) -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var thumb by remember { mutableStateOf<ImageBitmap?>(null) }
    var failed by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(PhotoFilter.ORIGINAL) }
    // Zoom on top of "cover" fit, and pan in px; both reset on rotate.
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var frame by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(source) {
        val loaded = withContext(Dispatchers.IO) { loadBitmap(context, source) }
        if (loaded == null) failed = true else {
            bitmap = loaded
            thumb = withContext(Dispatchers.Default) { thumbnail(loaded).asImageBitmap() }
        }
    }

    // Geometry of the image inside the frame.
    fun coverScale(b: Bitmap): Float = max(frame.width / b.width.toFloat(), frame.height / b.height.toFloat())
    fun clampPan(b: Bitmap, p: Offset, z: Float): Offset {
        val s = coverScale(b) * z
        val maxX = ((b.width * s - frame.width) / 2f).coerceAtLeast(0f)
        val maxY = ((b.height * s - frame.height) / 2f).coerceAtLeast(0f)
        return Offset(p.x.coerceIn(-maxX, maxX), p.y.coerceIn(-maxY, maxY))
    }

    fun save() {
        val b = bitmap ?: return
        if (frame == IntSize.Zero || saving) return
        saving = true
        val s = coverScale(b) * zoom
        val left = (frame.width - b.width * s) / 2f + pan.x
        val top = (frame.height - b.height * s) / 2f + pan.y
        val crop = CropFraction(
            left = (-left / s) / b.width,
            top = (-top / s) / b.height,
            width = (frame.width / s) / b.width,
            height = (frame.height / s) / b.height,
        )
        scope.launch {
            val uri = withContext(Dispatchers.Default) { runCatching { exportEdited(context, b, crop, filter) }.getOrNull() }
            saving = false
            if (uri != null) onDone(uri) else Toast.makeText(context, "Couldn't save the photo.", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = { if (!saving) onCancel() },
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel, enabled = !saving) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                }
                Text("Edit photo", color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Button(onClick = ::save, enabled = bitmap != null && !saving, modifier = Modifier.padding(end = 8.dp)) {
                    if (saving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Done")
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val b = bitmap
                when {
                    failed -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Couldn't open this photo.", color = Color.White)
                        TextButton(onClick = onCancel) { Text("Choose another") }
                    }
                    b == null -> CircularProgressIndicator(color = Color.White)
                    else -> {
                        val image = remember(b) { b.asImageBitmap() }
                        Box(
                            Modifier
                                .aspectRatio(FRAME_ASPECT)
                                .clip(RoundedCornerShape(16.dp))
                                .onSizeChanged { frame = it }
                                .pointerInput(b) {
                                    detectTransformGestures { _, panChange, zoomChange, _ ->
                                        val newZoom = (zoom * zoomChange).coerceIn(1f, MAX_ZOOM)
                                        zoom = newZoom
                                        pan = clampPan(b, pan + panChange, newZoom)
                                    }
                                }
                        ) {
                            Canvas(Modifier.fillMaxSize()) {
                                if (frame == IntSize.Zero) return@Canvas
                                val s = coverScale(b) * zoom
                                val w = (b.width * s).roundToInt()
                                val h = (b.height * s).roundToInt()
                                drawImage(
                                    image = image,
                                    dstOffset = IntOffset(
                                        ((size.width - w) / 2f + pan.x).roundToInt(),
                                        ((size.height - h) / 2f + pan.y).roundToInt()
                                    ),
                                    dstSize = IntSize(w, h),
                                    colorFilter = filter.colorFilter()
                                )
                                // Rule-of-thirds guides.
                                val guide = Color.White.copy(alpha = 0.35f)
                                for (i in 1..2) {
                                    val x = size.width * i / 3f
                                    val y = size.height * i / 3f
                                    drawLine(guide, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
                                    drawLine(guide, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
                                }
                            }
                        }
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    enabled = bitmap != null && !saving,
                    onClick = {
                        val b = bitmap ?: return@TextButton
                        scope.launch {
                            val rotated = withContext(Dispatchers.Default) { rotate(b, 90) }
                            bitmap = rotated
                            thumb = withContext(Dispatchers.Default) { thumbnail(rotated).asImageBitmap() }
                            zoom = 1f
                            pan = Offset.Zero
                        }
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Rotate", color = Color.White)
                }
                Text(
                    "Pinch and drag to frame",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(PhotoFilter.entries) { option ->
                    FilterThumb(thumb = thumb, filter = option, selected = option == filter) { filter = option }
                }
            }
        }
    }
}

@Composable
private fun FilterThumb(thumb: ImageBitmap?, filter: PhotoFilter, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            Modifier
                .size(64.dp)
                .clip(shape)
                .background(Color.DarkGray)
                .border(if (selected) 2.dp else 0.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, shape)
        ) {
            if (thumb != null) {
                Image(
                    bitmap = thumb,
                    contentDescription = filter.label,
                    contentScale = ContentScale.Crop,
                    colorFilter = filter.colorFilter(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            filter.label,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
