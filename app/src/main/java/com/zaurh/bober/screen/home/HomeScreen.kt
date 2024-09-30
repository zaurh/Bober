package com.zaurh.bober.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.zaurh.bober.screen.home.components.EmptyChatAlert
import com.zaurh.bober.screen.home.components.Home_MatchUserItem
import com.zaurh.bober.screen.home.components.SearchBar

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    chatScreenViewModel: ChatScreenViewModel
) {
    val messageList = chatScreenViewModel.state.value.messages
    val chatList = homeViewModel.chatList.collectAsState()
    val currentUser = homeViewModel.userDataState.collectAsState()
    val currentUserId = currentUser.value?.id ?: ""

    val matchListState = homeViewModel.matchListState.collectAsState()

    val searchValue = homeViewModel.searchQuery.value
    val context = LocalContext.current
    val chatState = chatScreenViewModel.state.value


    LaunchedEffect(messageList) {
        homeViewModel.getChatList()
        homeViewModel.getMatchedUsers()
    }


    BackHandler(
        onBack = {
            if (searchValue.isNotEmpty()){
                homeViewModel.onSearchChange("")
            }else{
                (context as? Activity)?.finish()
            }
        }
    )
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        currentUser.let { user ->
            val matchList = matchListState.value

            val filteredMatchList = matchList.filter { matchData ->
                matchData?.id !in chatList.value.map { it?.recipientId }
            }

            val newMatchesSize = matchList.count { it?.new == true }

            SearchBar(
                value = searchValue,
                onValueChange = homeViewModel::onSearchChange,
                placeHolder = "Search matches"
            )

            if (filteredMatchList.any { it?.new == true }){
                Spacer(modifier = Modifier.size(12.dp))
                Text(text = "New Matches ($newMatchesSize)", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.size(12.dp))
            }

            LazyRow {
                items(filteredMatchList) { match ->
                    match.let {
                        val matchedUser =
                            matchListState.value.find { it?.id == match?.id }

                        matchedUser?.let {
                            Home_MatchUserItem(
                                matchedUserData = matchedUser,
                                new = match?.new ?: false,
                                navController = navController,
                                homeViewModel = homeViewModel
                            )
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            LazyColumn {
                val filteredChatList = chatList.value.filter {
                        chat ->
                    chat?.recipientUsername?.contains(searchValue, ignoreCase = true) == true
                }
                item {
                    if (matchList.isEmpty()){
                        EmptyChatAlert()
//                        Text("You don't have any match yet.")
                    }
                }

                items(filteredChatList){ chatData ->
                    val recipientId = chatData?.recipientId ?: ""

                    val filteredMessageList = messageList.filter {
                        it.senderUserId == currentUser.value?.id && it.recipientUserId == recipientId
                                || it.senderUserId == recipientId && it.recipientUserId == currentUser.value?.id
                    }.reversed()

                    val recipientIsTyping =
                        if (chatState.recipientUserId == recipientId) chatState.recipientIsTyping
                            ?: false else false

                    val unReadMessages = filteredMessageList.filter { it.recipientUserId == currentUserId && it.status != MessageStatus.READ }
                    val newMessagesSize = unReadMessages.size

                    val lastMessage = filteredMessageList.lastOrNull()
                    val lastMessageIsMe = lastMessage?.senderUserId == currentUserId
                    val newMessage =
                        !lastMessageIsMe && lastMessage?.status != MessageStatus.READ && lastMessage != null

                    val lastMessageTime = lastMessage?.timestamp ?: ""
                    val lastMessageIsGif = lastMessage?.text?.endsWith(".gif") ?: false
                    val lastMessageText = if (lastMessageIsGif) "GIF" else lastMessage?.text ?: ""
                    val lastMessageStatus = lastMessage?.status ?: MessageStatus.SENT

                    chatData?.let {
                        HomeChatItem(
                            chatData = chatData,
                            lastMessage = lastMessageText,
                            lastMessageStatus = lastMessageStatus,
                            lastMessageSenderIsCurrent = lastMessageIsMe,
                            lastMessageTime = lastMessageTime,
                            newMessage = newMessage,
                            newMessagesSize = newMessagesSize,
                            typing = recipientIsTyping
                        ) {
                            navController.navigate(Screen.ChatScreen.createRoute(username = chatData.recipientUsername))
                            homeViewModel.switchRecipient(chatData.recipientId)
                        }
                    }

                }

//                items(filteredChatList.map { it?.recipientId }) { chatUserId ->
//
//                    val filteredList = messageList.filter {
//                        it.senderUserId == currentUser.value?.id && it.recipientUserId == chatUserId
//                                || it.senderUserId == chatUserId && it.recipientUserId == currentUser.value?.id
//                    }.reversed()
//
//                    val recipientIsTyping =
//                        if (chatState.recipientUserId == chatUserId) chatState.recipientIsTyping
//                            ?: false else false
//
//                    val lastMessage = filteredList.lastOrNull()
//                    val lastMessageTime = lastMessage?.timestamp
//                    val lastMessageIsMe = lastMessage?.senderUserId == currentUserId
//                    val lastMessageIsGif = lastMessage?.text?.endsWith(".gif") ?: false
//                    val lastMessageText = if (lastMessageIsGif) "GIF" else lastMessage?.text ?: ""
//                    val lastMessageStatus = lastMessage?.status ?: MessageStatus.SENT
//
//                    val unReadMessages = filteredList.filter { it.recipientUserId == currentUserId && it.status != MessageStatus.READ }
//                    val newMessagesSize = unReadMessages.size
//
//                    val newMessage =
//                        !lastMessageIsMe && lastMessage?.status != MessageStatus.READ && lastMessage != null
//
//                    val matchUser =
//                        matchListState.value.find { it?.userId == chatUserId }
//
//                    matchUser?.let {
//                        MatchUserItem(
//                            matchUser = matchUser,
//                            lastMessage = lastMessageText,
//                            lastMessageTime = lastMessageTime ?: "",
//                            lastMessageSenderIsCurrent = lastMessageIsMe,
//                            lastMessageStatus = lastMessageStatus,
//                            newMessage = newMessage,
//                            newMessagesSize = newMessagesSize,
//                            typing = recipientIsTyping
//                        ) {
//                            navController.navigate(Screen.ChatScreen.createRoute(username = matchUser.username))
//                        }
//                    }
//
//                }
            }
        }
    }
}





