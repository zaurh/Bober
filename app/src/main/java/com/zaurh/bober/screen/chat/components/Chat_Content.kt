package com.zaurh.bober.screen.chat.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun Chat_Content(
    chatScreenViewModel: ChatScreenViewModel,
    currentUser: UserData,
    profileId: String,
    backgroundColor: BackgroundColor,
    doodleEnabled: Boolean
) {

    val emptyChatGif = chatScreenViewModel.emptyChatGif.value
    val lazyState = rememberLazyListState()
    val context = LocalContext.current
    val chatState = chatScreenViewModel.chatState.value
    val profileData = chatScreenViewModel.chatListState.value.chatList.find { it.recipientId == profileId }

    var recipientMessages by remember { mutableStateOf<List<MessageData>>(listOf()) }
    var unReadMessages by remember { mutableStateOf<List<MessageData>>(listOf()) }
    val scope = rememberCoroutineScope()


    val gifState = chatScreenViewModel.gifState.value
    val gifs = chatScreenViewModel.gifUrls.collectAsState()




    LaunchedEffect(lazyState) {
        snapshotFlow { lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItems = lazyState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex != null && lastVisibleItemIndex == totalItems - 1) {
                    if (recipientMessages.size > 5) {
                        chatScreenViewModel.extendMessages(profileId)
                    }
                }
            }
    }

    LaunchedEffect(chatState.recipientMessages) {
        chatState.recipientMessages?.let { messages ->
            recipientMessages =
                messages.filter { (it.recipientUserId == profileId && it.senderUserId == currentUser.id) || (it.recipientUserId == currentUser.id && it.senderUserId == profileId) }
        }

    }

    LaunchedEffect(recipientMessages) {
        unReadMessages = recipientMessages.filter {
            it.senderUserId == profileId && it.recipientUserId == currentUser.id && it.status != MessageStatus.READ
        }
        if (unReadMessages.isNotEmpty()) {
            chatScreenViewModel.messageIsRead()
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (backgroundColor == BackgroundColor.DEFAULT) MaterialTheme.colorScheme.surface else backgroundColor.color)
    ) {
        if (doodleEnabled){
            Image(
                painter = painterResource(id = R.drawable.doodle),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.3f),
                contentScale = ContentScale.Crop
            )
        }

        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val chatContainsUser =
                    chatState.recipientMessages?.any { it.recipientUserId == profileId || it.senderUserId == profileId }
                if (!chatState.recipientMessagesLoading && chatState.recipientMessagesEmpty && chatContainsUser != true) {
                    LaunchedEffect(true) {
                        chatScreenViewModel.onEmptyMessageGif()
                    }
                    Text(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(20)
                            )
                            .background(MaterialTheme.colorScheme.background)
                            .padding(4.dp),
                        text = "Tap to send",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontFamily = outfitFamily
                    )
                    AnimatedGif(
                        context = context,
                        gifUrl = emptyChatGif.gifUrl,
                        size = 180,
                        circularSize = 40,
                        clipPercentage = 20,
                        onClick = {
                            chatScreenViewModel.onMessageChange(it)
                            chatScreenViewModel.sendMessage()
                        }
                    )

                    IconButton(onClick = {
                        chatScreenViewModel.onEmptyMessageGif()
                    }) {
                        Icon(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(4.dp)
                                .size(30.dp),
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                } else {
                    LazyColumnScrollbar(
                        state = lazyState, settings = ScrollbarSettings.Default.copy(
                            thumbThickness = 2.dp,
                            thumbSelectedColor = colorResource(id = R.color.backgroundBottom),
                            thumbUnselectedColor = Color.Gray
                        )
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            if (chatState.recipientMessagesLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            LazyColumn(
                                modifier = Modifier,
                                state = lazyState,
                                reverseLayout = true,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(groupMessagesWithHeaders(recipientMessages.reversed()).reversed()) {
                                    when (it) {
                                        is String -> {
                                            Text(
                                                text = it,
                                                fontSize = 12.sp,
                                                color = Color.LightGray,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20))
                                                    .background(Color.DarkGray)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                                textAlign = TextAlign.Center,
                                                fontFamily = outfitFamily
                                            )
                                        }

                                        is MessageData -> {
                                            val replyingText = recipientMessages.find { message ->
                                                message.id == it.replyingMessageId
                                            }

                                            Chat_MessageItem(
                                                messageData = it,
                                                currentUser = currentUser,
                                                replyingText = replyingText?.text ?: "",
                                                replyingState = chatScreenViewModel.replyingState.value,
                                                selectedMessages = chatScreenViewModel.selectedMessages.value,
                                                onSwipe = {
                                                    chatScreenViewModel.enableReply(
                                                        username = it.senderUsername ?: "",
                                                        messageId = it.id ?: "",
                                                        messageText = it.text ?: ""
                                                    )
                                                },
                                                onReplyMessageClick = { triggeredMessage ->
                                                    scope.launch {
                                                        val triggeredMessageIndex = recipientMessages.indexOf(replyingText)
                                                        if (triggeredMessageIndex != -1) {
                                                            lazyState.animateScrollToItem(triggeredMessageIndex)
                                                            Log.d("IndexBober", "Item found at index $triggeredMessageIndex")
                                                        } else {
                                                            Log.d("IndexBober", "Item not found in the list.")
                                                        }

                                                    }
                                                    chatScreenViewModel.triggerReplyingMessage(triggeredMessage)
                                                },
                                                onEmoteClick = { emote ->
                                                    chatScreenViewModel.selectMessage(it)
                                                    chatScreenViewModel.emoteMessage(
                                                        senderId = emote.senderId,
                                                        recipientId = profileId,
                                                        messageId = it.id ?: "",
                                                        emoteData = emote
                                                    )
                                                },
                                                onEmotesClick = {
                                                    chatScreenViewModel.openEmoteModalSheet(it)
                                                },
                                                onMessageClick = {
                                                    if (chatScreenViewModel.selectedMessages.value.isNotEmpty()){
                                                        chatScreenViewModel.selectMessage(it)
                                                    }
                                                },
                                                onMessageLongClick = {
                                                    chatScreenViewModel.selectMessage(it)
                                                }
                                            )
                                        }
                                    }

                                }
                            }
                        }

                    }

                }

            }

            AnimatedVisibility(
                visible = chatScreenViewModel.replyingState.value.enabled
            ) {
                Row(
                    Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.Start)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.size(4.dp))
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(id = R.drawable.reply_ic),
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(Modifier.size(4.dp))

                    Column(
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Text(
                            text = chatScreenViewModel.replyingState.value.username,
                            textAlign = TextAlign.Start,
                            color = Color.LightGray
                        )
                        val messageText = chatScreenViewModel.replyingState.value.messageText
                        Text(
                            text = if (messageText.startsWith("https://media.tenor.com") && messageText.endsWith(".gif")) "GIF" else messageText,
                            textAlign = TextAlign.Start,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                    IconButton(onClick = {
                        chatScreenViewModel.closeReply()
                    }) {
                        Icon(
                            imageVector = Icons.TwoTone.Close,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            }

            AnimatedVisibility(visible = gifState) {
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .weight(3f)
                ) {
                    items(gifs.value) {
                        AnimatedGif(
                            context = context,
                            gifUrl = it,
                            size = 80,
                            circularSize = 40,
                            clipPercentage = 20
                        ) {
                            chatScreenViewModel.onMessageChange(it)
                            chatScreenViewModel.sendMessage()
                        }
                    }
                }
            }


        }
    }

}


fun groupMessagesWithHeaders(messages: List<MessageData>): List<Any> {
    val groupedMessages = mutableListOf<Any>()
    var lastDateLabel: String? = null

    for (message in messages) {
        val dateLabel = getMessageDateLabel(message.timestamp?.toLong() ?: 0)
        if (dateLabel != lastDateLabel) {
            groupedMessages.add(dateLabel) // Add date header
            lastDateLabel = dateLabel
        }
        groupedMessages.add(message) // Add the message
    }

    return groupedMessages
}

fun getMessageDateLabel(timestamp: Long): String {
    val messageDate = Date(timestamp)
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())
    return when {
        isSameDay(messageDate, today.time) -> "Today"
        isSameDay(messageDate, yesterday.time) -> "Yesterday"
        else -> dateFormat.format(messageDate)
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}