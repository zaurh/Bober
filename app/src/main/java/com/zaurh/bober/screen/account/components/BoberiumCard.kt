package com.zaurh.bober.screen.account.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.PremiumCardData
import com.zaurh.bober.ui.theme.BrandFont
import com.zaurh.bober.ui.theme.BoberPalette
import kotlinx.coroutines.launch
import kotlin.math.ceil

private val CardShape = RoundedCornerShape(20.dp)
private val CardBrush = Brush.linearGradient(listOf(BoberPalette.Purple, BoberPalette.Pink, BoberPalette.PinkStrong))
private const val DAY_MILLIS = 24 * 60 * 60 * 1000.0
private val Perks = listOf("Unlimited likes", "See who likes you", "Read receipts", "Online and last seen")

/** Whole days until [PremiumCardData.expiryDate], rounded up so "expires today" still reads 1. */
fun PremiumCardData.daysLeft(now: Long = System.currentTimeMillis()): Int =
    ceil((expiryDate - now) / DAY_MILLIS).toInt().coerceAtLeast(0)

/**
 * The Boberium membership card. The front lists the perks and the status ([footer]); the back is the
 * card's reverse with a magnetic strip, shown while it's flipped (see [RotatableBoberiumCard]).
 */
@Composable
fun BoberiumCard(
    footer: String,
    modifier: Modifier = Modifier,
    frontSide: Boolean = true,
    action: (@Composable () -> Unit)? = null,
) {
    // The front always takes part in layout so both sides are the same size; its content defines the height.
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 190.dp)
            .shadow(12.dp, CardShape)
            .clip(CardShape)
            .background(CardBrush)
    ) {
        Column(
            Modifier
                .padding(18.dp)
                .alpha(if (frontSide) 1f else 0f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.bober_ic), contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(Modifier.size(6.dp))
                Text("Boberium", color = Color.White, style = TextStyle(fontFamily = BrandFont, fontSize = 24.sp))
            }
            Spacer(Modifier.height(12.dp))
            Perks.forEach { perk ->
                Text("•  $perk", color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(14.dp))
            Row(
                Modifier.fillMaxWidth().heightIn(min = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(footer, color = Color.White, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                if (frontSide) action?.invoke()
            }
        }
        if (!frontSide) {
            // The reverse is drawn mirrored so it reads correctly once the card is turned over.
            Column(Modifier.matchParentSize().graphicsLayer(scaleX = -1f)) {
                Spacer(Modifier.height(22.dp))
                Box(Modifier.fillMaxWidth().height(40.dp).background(BoberPalette.Navy))
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier
                        .padding(horizontal = 18.dp)
                        .fillMaxWidth(0.7f)
                        .height(28.dp)
                        .background(Color.White.copy(alpha = 0.9f))
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "This card is your key to Bober's extra features. Thanks for being part of the community.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }
        }
    }
}

/** The card spins in once, and can be turned over by dragging; it springs back when released. */
@Composable
fun RotatableBoberiumCard(footer: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val rotationX = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotationY.animateTo(360f, tween(durationMillis = 1800, easing = FastOutSlowInEasing))
        rotationY.snapTo(0f)
    }

    // Facing the viewer when both angles (mod 360) are within ±90°, or both are turned past it.
    fun facing(angle: Float): Boolean {
        val a = ((angle % 360f) + 360f) % 360f
        return a < 90f || a > 270f
    }
    val front = facing(rotationX.value) == facing(rotationY.value)

    BoberiumCard(
        footer = footer,
        frontSide = front,
        modifier = modifier
            .graphicsLayer {
                this.rotationX = rotationX.value
                this.rotationY = rotationY.value
                cameraDistance = 16 * density
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, drag ->
                        change.consume()
                        scope.launch {
                            rotationX.snapTo(rotationX.value - drag.y / 4f)
                            rotationY.snapTo(rotationY.value + drag.x / 4f)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            // Settle on the nearest whole turn so it ends face up.
                            launch { rotationX.animateTo(nearestTurn(rotationX.value)) }
                            launch { rotationY.animateTo(nearestTurn(rotationY.value)) }
                        }
                    }
                )
            }
    )
}

private fun nearestTurn(angle: Float): Float = Math.round(angle / 360f) * 360f

/** Account screen entry: claim the free Boberium card, or see how long the active one lasts. */
@Composable
fun BoberiumSection(
    activeCard: PremiumCardData?,
    activating: Boolean,
    onActivate: () -> Unit,
    onOpenCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (activeCard != null) {
        val days = activeCard.daysLeft()
        BoberiumCard(
            footer = "Active · $days ${if (days == 1) "day" else "days"} left",
            modifier = modifier,
            action = {
                TextButton(onClick = onOpenCard) { Text("View", color = Color.White) }
            }
        )
    } else {
        BoberiumCard(
            footer = "Free for 30 days",
            modifier = modifier,
            action = {
                Button(
                    onClick = onActivate,
                    enabled = !activating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BoberPalette.Navy)
                ) {
                    if (activating) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = BoberPalette.Navy)
                    else Text("Activate")
                }
            }
        )
    }
}

/** Shown right after activating (with congratulations) or when viewing the active card. */
@Composable
fun BoberiumCardDialog(card: PremiumCardData, justActivated: Boolean, onDismiss: () -> Unit) {
    val days = card.daysLeft()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = if (justActivated) {
            { Text("🎉 Boberium unlocked!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) }
        } else null,
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (justActivated) {
                    Text("Enjoy unlimited likes and every premium perk.", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                }
                RotatableBoberiumCard(footer = "Expires in $days ${if (days == 1) "day" else "days"}")
                Spacer(Modifier.height(12.dp))
                Text(
                    "Drag the card to turn it over",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(if (justActivated) "Awesome" else "Close") } }
    )
}
