package com.zaurh.bober.screen.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.ui.theme.BoberPalette
import com.zaurh.bober.ui.theme.PhotoScrim

/** Drag distance (px) at which the LIKE / NOPE stamp is fully visible. */
private const val STAMP_FULL_AT = 260f

/**
 * One profile card on the discover deck. Tap the left/right half of the photo to browse photos;
 * [dragX] is the card's current horizontal drag, used to fade in the LIKE / NOPE stamps.
 */
@Composable
fun MatchCardItem(
    boberData: BoberData,
    dragX: Float,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val photos = boberData.image.ifEmpty { listOf("") }
    var photo by remember(boberData.userId) { mutableIntStateOf(0) }

    Box(
        modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = photos[photo.coerceIn(photos.indices)],
            contentDescription = boberData.name.ifEmpty { boberData.username },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.placeholder_image),
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(photos.size) {
                    detectTapGestures { offset ->
                        photo = if (offset.x < size.width / 2) {
                            (photo - 1).coerceAtLeast(0)
                        } else {
                            (photo + 1).coerceAtMost(photos.lastIndex)
                        }
                    }
                }
        )

        // Photo position bars
        if (photos.size > 1) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                photos.indices.forEach { index ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (index == photo) Color.White else Color.White.copy(alpha = 0.35f))
                    )
                }
            }
        }

        // Name and details over a scrim
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PhotoScrim)
                .padding(start = 20.dp, end = 12.dp, top = 80.dp, bottom = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            boberData.name.ifEmpty { boberData.username },
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (boberData.age > 0) {
                            Spacer(Modifier.size(8.dp))
                            Text(
                                "${boberData.age}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                    if (boberData.jobTitle.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Detail(Icons.Outlined.Build, boberData.jobTitle)
                    }
                    if (boberData.distance > 0) {
                        Spacer(Modifier.height(4.dp))
                        Detail(Icons.Default.LocationOn, if (boberData.distance < 2) "Less than 1 km away" else "${boberData.distance} km away")
                    }
                    if (boberData.aboutMe.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            boberData.aboutMe,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = onOpenProfile) {
                    Icon(Icons.Outlined.Info, contentDescription = "Open profile", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }

        Stamp(
            text = "LIKE",
            color = BoberPalette.Like,
            alpha = (dragX / STAMP_FULL_AT).coerceIn(0f, 1f),
            rotation = -14f,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 40.dp)
        )
        Stamp(
            text = "NOPE",
            color = BoberPalette.Pass,
            alpha = (-dragX / STAMP_FULL_AT).coerceIn(0f, 1f),
            rotation = 14f,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 40.dp)
        )
    }
}

@Composable
private fun Detail(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(16.dp))
        Spacer(Modifier.size(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun Stamp(text: String, color: Color, alpha: Float, rotation: Float, modifier: Modifier) {
    if (alpha <= 0f) return
    Text(
        text,
        color = color,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .graphicsLayer { this.alpha = alpha }
            .rotate(rotation)
            .border(4.dp, color, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 4.dp)
    )
}
