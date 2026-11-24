package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.home.tickIcon
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val SwipeToReplyDistance = 64.dp

/**
 * One chat bubble. Long-press opens the actions (react / reply / copy); swiping it to the right
 * starts a reply. Shows the quoted message it replies to and its reactions.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Chat_MessageItem(
    messageData: MessageData,
    currentUserId: String,
    /** False when the next (older) message is from the same sender, so bubbles group together. */
    showTail: Boolean = true,
    /** The message this one replies to, if it's loaded. */
    repliedMessage: MessageData? = null,
    repliedAuthor: String = "",
    highlighted: Boolean = false,
    /** Color for timestamps drawn on the chat background (changes with custom backgrounds). */
    metaColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onLongPress: () -> Unit = {},
    onSwipeReply: () -> Unit = {},
    onQuoteClick: () -> Unit = {},
    onReactionsClick: () -> Unit = {},
) {
    val mine = messageData.senderUserId == currentUserId
    val text = messageData.text.orEmpty()
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp * 0.75f
    val haptics = LocalHapticFeedback.current

    val big = 20.dp
    val small = 6.dp
    val shape = if (mine) {
        RoundedCornerShape(big, big, if (showTail) small else big, big)
    } else {
        RoundedCornerShape(big, big, big, if (showTail) small else big)
    }
    val container = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val content = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val highlight by animateColorAsState(
        if (highlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
        label = "highlight"
    )

    // Swipe right to reply.
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val threshold = with(LocalDensity.current) { SwipeToReplyDistance.toPx() }

    Box(
        Modifier
            .fillMaxWidth()
            .background(highlight)
            .pointerInput(messageData.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX.value >= threshold) onSwipeReply()
                        scope.launch { offsetX.animateTo(0f) }
                    },
                    onDragCancel = { scope.launch { offsetX.animateTo(0f) } },
                    onHorizontalDrag = { change, dragAmount ->
                        val next = (offsetX.value + dragAmount).coerceIn(0f, threshold * 1.3f)
                        if (offsetX.value < threshold && next >= threshold) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        change.consume()
                        scope.launch { offsetX.snapTo(next) }
                    }
                )
            }
    ) {
        Icon(
            painterResource(R.drawable.reply_ic),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(20.dp)
                .alpha((offsetX.value / threshold).coerceIn(0f, 1f))
        )
        Row(
            Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .padding(horizontal = 12.dp, vertical = if (showTail) 4.dp else 1.dp),
            horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
        ) {
            Column(horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
                if (messageData.replyingMessageId != null) {
                    QuotedMessage(
                        author = repliedAuthor,
                        text = repliedMessage?.preview ?: "Original message",
                        onClick = onQuoteClick,
                        modifier = Modifier.widthIn(max = maxWidth)
                    )
                    Spacer(Modifier.height(2.dp))
                }

                val pressModifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    }
                )
                if (messageData.isGif) {
                    Box(Modifier.clip(MaterialTheme.shapes.medium).then(pressModifier)) {
                        AnimatedGif(context = LocalContext.current, gifUrl = text, size = 160)
                    }
                } else {
                    Column(
                        Modifier
                            .widthIn(max = maxWidth)
                            .clip(shape)
                            .background(container)
                            .then(pressModifier)
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(text, color = content, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                if (messageData.emoted.isNotEmpty()) {
                    Reactions(
                        messageData = messageData,
                        currentUserId = currentUserId,
                        onClick = onReactionsClick,
                        modifier = Modifier.offset(y = (-6).dp).padding(horizontal = 8.dp)
                    )
                }

                if (showTail) {
                    Row(Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            messageData.timestamp.orEmpty(),
                            style = MaterialTheme.typography.labelSmall,
                            color = metaColor
                        )
                        if (mine) {
                            Spacer(Modifier.size(4.dp))
                            Image(painterResource(messageData.status.tickIcon()), contentDescription = messageData.status.name, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

/** The replied-to message above a bubble: accent bar, author and a one-line preview. Tapping scrolls to it. */
@Composable
private fun QuotedMessage(author: String, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(end = 12.dp)
    ) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Column(Modifier.padding(start = 9.dp, top = 6.dp, bottom = 6.dp)) {
            if (author.isNotEmpty()) {
                Text(author, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            }
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Reaction pill under a bubble: each distinct emoji once, plus a count when more than one person reacted. */
@Composable
private fun Reactions(messageData: MessageData, currentUserId: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val reactedByMe = messageData.emoted.any { it.senderId == currentUserId }
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (reactedByMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                messageData.emoted.map { it.emoteSealed.displayName }.distinct().joinToString(""),
                style = MaterialTheme.typography.bodyMedium
            )
            if (messageData.emoted.size > 1) {
                Spacer(Modifier.size(4.dp))
                Text(
                    "${messageData.emoted.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
