package com.zaurh.bober.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.home.components.SearchBar
import com.zaurh.bober.ui.components.EmptyState
import com.zaurh.bober.ui.components.SectionHeader

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    chatScreenViewModel: ChatScreenViewModel
) {
    val context = LocalContext.current
    val chatState = chatScreenViewModel.state.value
    val messages = chatState.messages
    val chats = homeViewModel.chatList.collectAsState().value.filterNotNull()
    val matches = homeViewModel.matchListState.collectAsState().value.filterNotNull()
    val currentUserId = homeViewModel.userDataState.collectAsState().value?.id.orEmpty()
    val query = homeViewModel.searchQuery.value

    // New messages can mean new conversations or matches, so refresh both.
    LaunchedEffect(messages) {
        homeViewModel.getChatList()
        homeViewModel.getMatchedUsers()
    }

    BackHandler {
        if (query.isNotEmpty()) homeViewModel.onSearchChange("") else (context as? Activity)?.finish()
    }

    fun openChat(userId: String, username: String) {
        homeViewModel.switchRecipient(userId)
        navController.navigate(Screen.ChatScreen.createRoute(username = username))
    }

    val chattingWith = chats.map { it.recipientId }.toSet()
    val newMatches = matches
        .filter { it.id !in chattingWith }
        .filter { query.isEmpty() || it.username.contains(query, true) || it.name.contains(query, true) }
    val visibleChats = chats
        .filter { query.isEmpty() || it.recipientUsername.contains(query, true) }
        .sortedByDescending { it.lastUpdated }

    if (matches.isEmpty() && chats.isEmpty()) {
        EmptyState(
            title = "No matches yet",
            message = "Swipe right on Discover. When someone likes you back, they'll show up here."
        )
        return
    }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            SearchBar(
                value = query,
                onValueChange = homeViewModel::onSearchChange,
                placeHolder = "Search matches",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (newMatches.isNotEmpty()) {
            item {
                SectionHeader(
                    "New matches",
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                )
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(newMatches, key = { it.id }) { match ->
                        MatchBubble(
                            image = match.image,
                            name = match.name.ifEmpty { match.username },
                            new = match.new,
                            onClick = {
                                if (match.new) homeViewModel.newMatchToOld(match.id)
                                openChat(match.id, match.username)
                            }
                        )
                    }
                }
            }
        }

        if (visibleChats.isNotEmpty()) {
            item {
                SectionHeader(
                    "Messages",
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp)
                )
            }
        }

        items(visibleChats, key = { it.recipientId }) { chat ->
            val conversation = messages.filter {
                (it.senderUserId == currentUserId && it.recipientUserId == chat.recipientId) ||
                        (it.senderUserId == chat.recipientId && it.recipientUserId == currentUserId)
            }
            // Messages are newest-first.
            val last = conversation.firstOrNull()
            val unread = conversation.count { it.recipientUserId == currentUserId && it.status != MessageStatus.READ }
            val lastIsMine = last?.senderUserId == currentUserId
            val text = last?.text.orEmpty()

            HomeChatItem(
                chatData = chat,
                lastMessage = if (text.endsWith(".gif")) "GIF" else text,
                lastMessageStatus = last?.status ?: MessageStatus.SENT,
                lastMessageTime = last?.timestamp.orEmpty(),
                lastMessageSenderIsCurrent = lastIsMine,
                typing = chatState.recipientUserId == chat.recipientId && chatState.recipientIsTyping == true,
                newMessage = unread > 0,
                newMessagesSize = unread,
                onClick = { openChat(chat.recipientId, chat.recipientUsername) }
            )
        }
    }
}
