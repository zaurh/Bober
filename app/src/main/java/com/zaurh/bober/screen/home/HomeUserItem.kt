package com.zaurh.bober.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.ChatData

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
            .padding(8.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            val profilePic = chatData.recipientImage
            AsyncImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                model = profilePic,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            if (newMessage) {
                Icon(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.online_ic),
                    contentDescription = "",
                    tint = colorResource(id = R.color.backgroundTop)
                )
            }

        }

        Spacer(modifier = Modifier.size(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = chatData.recipientUsername,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (typing) {
                    Text(text = "typing...", fontSize = 14.sp, color = Color.Gray)

                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val tickIcon = when (lastMessageStatus) {
                            MessageStatus.SENT -> R.drawable.sent_ic
                            MessageStatus.DELIVERED -> R.drawable.delivered_ic
                            MessageStatus.READ -> R.drawable.read_ic
                            MessageStatus.FAILED -> R.drawable.sent_ic
                        }
                        if (lastMessageSenderIsCurrent) {
                            Image(
                                painterResource(id = tickIcon),
                                contentDescription = "message_status",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = lastMessage,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

            }
            Column(
                modifier = Modifier.wrapContentWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = lastMessageTime,
                    color = if (newMessage) colorResource(id = R.color.backgroundTop) else Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.size(4.dp))
                if (newMessagesSize > 0) {
                    Text(
                        modifier = Modifier
                            .clip(
                                CircleShape
                            )
                            .background(colorResource(id = R.color.backgroundTop))
                            .padding(start = 6.dp, end = 6.dp),
                        text = "$newMessagesSize",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}