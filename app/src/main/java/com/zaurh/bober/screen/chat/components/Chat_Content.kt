package com.zaurh.bober.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.ChatBackground
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.ui.components.Avatar
import com.zaurh.bober.ui.components.EmptyState
import com.zaurh.bober.ui.theme.color
import com.zaurh.bober.ui.theme.contentColorOn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PAGE = 30

@Composable
fun Chat_Content(
    chatScreenViewModel: ChatScreenViewModel,
    userData: UserData,
    currentUser: UserData,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Newest first; the list is drawn bottom-up (reverseLayout).
    val conversation = chatScreenViewModel.state.value.messages.filter {
        (it.senderUserId == currentUser.id && it.recipientUserId == userData.id) ||
                (it.senderUserId == userData.id && it.recipientUserId == currentUser.id)
    }
    var limit by remember { mutableIntStateOf(PAGE) }
    val visible = conversation.take(limit)
    val listState = rememberLazyListState()
    var highlightedId by remember { mutableStateOf<String?>(null) }

    // The current user's background for this chat (stored per chat on the server).
    val chat = chatScreenViewModel.chatList.collectAsState().value.firstOrNull { it?.recipientId == userData.id }
    val background = chat?.backgroundColor ?: ChatBackground.DEFAULT
    val backgroundColor = background.color ?: MaterialTheme.colorScheme.surface
    val onBackground = if (background.color != null) contentColorOn(backgroundColor) else MaterialTheme.colorScheme.onSurface
    val metaColor = if (background.color != null) onBackground.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant

    // Load more when the oldest loaded message scrolls into view.
    LaunchedEffect(listState, conversation.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { last -> if (last != null && last >= visible.size - 1 && limit < conversation.size) limit += PAGE }
    }
    // Jump to the newest message when one arrives.
    LaunchedEffect(conversation.firstOrNull()?.id) { listState.animateScrollToItem(0) }

    fun scrollToMessage(id: String) {
        val index = conversation.indexOfFirst { it.id == id }
        if (index < 0) return
        if (index >= limit) limit = index + PAGE
        scope.launch {
            listState.animateScrollToItem(index)
            highlightedId = id
            delay(1200)
            if (highlightedId == id) highlightedId = null
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            if (chat?.doodleEnabled == true) {
                Image(
                    painter = painterResource(R.drawable.doodle),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(onBackground.copy(alpha = 0.07f)),
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (conversation.isEmpty() && !userData.id.isNullOrEmpty()) {
                EmptyState(
                    title = "You matched with ${userData.name?.ifEmpty { null } ?: userData.username}",
                    message = "Don't be shy, say hi!",
                    action = { Avatar(userData.imageUrl?.firstOrNull(), 96.dp) }
                )
            } else {
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(visible, key = { index, message -> message.id ?: "i$index" }) { index, message ->
                        // In reverse layout the item drawn just below this one is index - 1 (newer).
                        val newer = visible.getOrNull(index - 1)
                        val replied = message.replyingMessageId?.let { id -> conversation.firstOrNull { it.id == id } }
                        Chat_MessageItem(
                            messageData = message,
                            currentUserId = currentUser.id.orEmpty(),
                            showTail = newer == null || newer.senderUserId != message.senderUserId,
                            repliedMessage = replied,
                            repliedAuthor = when {
                                replied == null -> ""
                                replied.senderUserId == currentUser.id -> "You"
                                else -> userData.name?.ifEmpty { null } ?: userData.username.orEmpty()
                            },
                            highlighted = message.id != null && message.id == highlightedId,
                            metaColor = metaColor,
                            onLongPress = { chatScreenViewModel.openMessageActions(message) },
                            onSwipeReply = { chatScreenViewModel.startReply(message) },
                            onQuoteClick = { message.replyingMessageId?.let(::scrollToMessage) },
                            onReactionsClick = { chatScreenViewModel.openReactions(message) },
                        )
                    }
                }
            }
        }

        val gifState = chatScreenViewModel.gifState.value
        val gifs = chatScreenViewModel.gifUrls.collectAsState()
        AnimatedVisibility(visible = gifState) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                items(gifs.value) { gif ->
                    AnimatedGif(context = context, gifUrl = gif, size = 110) {
                        chatScreenViewModel.onMessageChange(it)
                        chatScreenViewModel.sendMessage()
                    }
                }
            }
        }
    }
}
