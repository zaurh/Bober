package com.zaurh.bober.screen.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.home.tickIcon

@Composable
fun Chat_MessageItem(
    messageData: MessageData,
    currentUserId: String,
    /** False when the next (older) message is from the same sender, so bubbles group together. */
    showTail: Boolean = true,
) {
    val mine = messageData.senderUserId == currentUserId
    val text = messageData.text.orEmpty()
    val isGif = text.endsWith(".gif")
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp * 0.75f

    val big = 20.dp
    val small = 6.dp
    val shape = if (mine) {
        RoundedCornerShape(big, big, if (showTail) small else big, big)
    } else {
        RoundedCornerShape(big, big, big, if (showTail) small else big)
    }
    val container = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val content = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = if (showTail) 4.dp else 1.dp),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
    ) {
        Column(horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
            if (isGif) {
                AnimatedGif(context = LocalContext.current, gifUrl = text, size = 160)
            } else {
                Column(
                    Modifier
                        .widthIn(max = maxWidth)
                        .clip(shape)
                        .background(container)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(text, color = content, style = MaterialTheme.typography.bodyLarge)
                }
            }
            if (showTail) {
                Row(Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        messageData.timestamp.orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
