package com.zaurh.bober.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.ui.components.Avatar
import com.zaurh.bober.ui.components.CountBadge

/** One conversation in the chat list. */
@Composable
fun HomeChatItem(
    chatData: ChatData,
    lastMessage: String,
    lastMessageStatus: MessageStatus,
    lastMessageTime: String,
    lastMessageSenderIsCurrent: Boolean,
    typing: Boolean,
    newMessage: Boolean,
    newMessagesSize: Int,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(url = chatData.recipientImage, size = 56.dp, highlighted = newMessage)
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                chatData.recipientUsername,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                when {
                    typing -> Text("typing…", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    lastMessage.isEmpty() -> Text("Say hi 👋", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> {
                        if (lastMessageSenderIsCurrent) {
                            Image(painterResource(lastMessageStatus.tickIcon()), contentDescription = lastMessageStatus.name, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                        }
                        Text(
                            lastMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (newMessage) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (newMessage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                lastMessageTime,
                style = MaterialTheme.typography.labelSmall,
                color = if (newMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            if (newMessagesSize > 0) CountBadge(newMessagesSize) else Spacer(Modifier.height(20.dp))
        }
    }
}

/** A new match in the horizontal row above the conversations. */
@Composable
fun MatchBubble(
    image: String,
    name: String,
    new: Boolean,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .width(76.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Avatar(url = image, size = 68.dp, highlighted = new)
        Spacer(Modifier.height(6.dp))
        Text(
            name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

fun MessageStatus.tickIcon(): Int = when (this) {
    MessageStatus.SENT, MessageStatus.FAILED -> R.drawable.sent_ic
    MessageStatus.DELIVERED -> R.drawable.delivered_ic
    MessageStatus.READ -> R.drawable.read_ic
    MessageStatus.SENDING -> R.drawable.pending_clock
}
