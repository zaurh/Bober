package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.message.EmoteSealed
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.message.formatTimestampToTime
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.chat.state.ReplyingState
import com.zaurh.bober.ui.theme.outfitFamily
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Chat_MessageItem(
    messageData: MessageData,
    currentUser: UserData,
    replyingText: String,
    replyingState: ReplyingState,
    selectedMessages: List<MessageData>,
    onMessageClick: () -> Unit,
    onMessageLongClick: () -> Unit,
    onReplyMessageClick: (String) -> Unit,
    onSwipe: () -> Unit,
    onEmoteClick: (EmoteData) -> Unit,
    onEmotesClick: () -> Unit
) {
    val senderIsCurrent = messageData.senderUserId == currentUser.id
    val messageText = messageData.text ?: ""
    val messageIsGif = (messageText.endsWith(".gif") && messageText.startsWith("https://media.tenor.com"))
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var emotionState by remember { mutableStateOf(false) }



    val reply = SwipeAction(
        icon = {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.reply_ic),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        background = Color.Transparent,
        onSwipe = {
            onSwipe()
        }
    )


    SwipeableActionsBox(
        swipeThreshold = 10.dp,
        backgroundUntilSwipeThreshold = Color.Transparent,
        startActions = listOf(reply),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onMessageClick()
                    },
                    onLongClick = {
                        emotionState = true
                        onMessageLongClick()
                    }
                )
                .background(
                    if ((replyingState.replyingMessageTriggered && replyingState.triggeredMessageId == messageData.id) || messageData in selectedMessages) {
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f)
                    }
                    else Color.Transparent
                )
                .padding(8.dp),
            horizontalAlignment = if (senderIsCurrent) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            if (!messageData.replyingMessageId.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.reply_ic),
                        contentDescription = "",
                        tint = Color.Gray
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .background(Color.Gray)
                        .clickable {
                            onReplyMessageClick(messageData.replyingMessageId ?: "")
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                        text = if (replyingText.startsWith("https://media.tenor.com") && replyingText.endsWith(".gif")) "GIF" else replyingText,
                        color = Color.White,
                        fontFamily = outfitFamily
                    )
                }
                Spacer(Modifier.size(4.dp))
            }
            AnimatedVisibility(visible = (messageData in selectedMessages && selectedMessages.size < 2)) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    EmoteSealed.entries.forEach {
                        Text(modifier = Modifier.clickable {
                            emotionState = false
                            onEmoteClick(
                                EmoteData(
                                    emoteSealed = it,
                                    senderId = currentUser.id ?: "",
                                    senderUsername = currentUser.username ?: "",
                                    senderImage = currentUser.imageUrl?.firstOrNull() ?: ""
                                )
                            )
                        }, text = it.displayName, fontSize = 28.sp, fontFamily = outfitFamily)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .widthIn(max = screenWidth * 0.8f) // Limit width to 80% of screen width
                    .clip(RoundedCornerShape(if (messageIsGif) 5 else 10))
                    .background(
                        if (senderIsCurrent)
                            colorResource(id = R.color.darkGreen) else Color.DarkGray
                    )
                    .padding(if (messageIsGif) 0.dp else 8.dp)
                ,
                horizontalAlignment = if (senderIsCurrent) Alignment.End else Alignment.Start
            ) {
                if (messageIsGif) {
                    MessageGif(
                        context = context,
                        gifUrl = messageText
                    )
                } else {
                    Text(
                        text = messageText,
                        color = Color.White,
                        fontFamily = outfitFamily
                    )
                }

                Spacer(modifier = Modifier.size(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatTimestampToTime(messageData.timestamp?.toLong() ?: 0),
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontFamily = outfitFamily
                    )
                    if (senderIsCurrent) {
                        val tickIcon = when (messageData.status) {
                            MessageStatus.SENT -> R.drawable.sent_ic
                            MessageStatus.DELIVERED -> R.drawable.delivered_ic
                            MessageStatus.READ -> R.drawable.read_ic
                            MessageStatus.FAILED -> R.drawable.sent_ic
                            MessageStatus.SENDING -> R.drawable.pending_clock
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        Image(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(id = tickIcon),
                            contentDescription = ""
                        )
                    }
                }


            }
            AnimatedVisibility(visible = messageData.emoted?.isNotEmpty() == true) {
                Row(modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .background(Color.DarkGray.copy(alpha = 0.9f))
                    .padding(4.dp)
                    .clickable {
                        onEmotesClick()
                    }) {
                    messageData.emoted?.forEach {
                        Text(text = it.emoteSealed.displayName, fontFamily = outfitFamily)
                    }
                }
            }


        }

    }
}