package com.zaurh.bober.screen.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun HomeScreen(
    navController: NavController,
    deepLink: String,
    homeViewModel: HomeViewModel,
    chatScreenViewModel: ChatScreenViewModel
) {
    val currentUser = homeViewModel.userDataState.collectAsState()
    val currentUserId = currentUser.value?.id ?: ""

    val searchValue = homeViewModel.searchQuery.value
    val context = LocalContext.current
    val chatState = chatScreenViewModel.chatState.value
    val chatListState = chatScreenViewModel.chatListState.value
    val matchListState = chatScreenViewModel.matchListState.value
    val chatList = chatListState.chatList


    LaunchedEffect(true) {
        if (matchListState.matchList.isEmpty()){
            chatScreenViewModel.getMatchList()
        }

        homeViewModel.storeFCMToken()
        chatScreenViewModel.onRouteChange(
            onChatScreen = false,
            recipientId = ""
        )
        chatScreenViewModel.clearProfileData()
        chatScreenViewModel.clearEmptyMessages()
    }

//    LaunchedEffect(chatList) {
//        if (chatState.recipientMessages.isNullOrEmpty()){
//            chatScreenViewModel.chatListState.value.chatList.map { it.recipientId }.forEach {
//                chatScreenViewModel.getRecipientMessages(it)
//                Log.d("dsjkdsljkd", "2 - ${chatState.recipientMessages}")
//
//            }
//        }
//    }


    BackHandler(
        onBack = {
            if (searchValue.isNotEmpty()) {
                homeViewModel.onSearchChange("")
            } else {
                (context as? Activity)?.finish()
            }
        }
    )
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        ) {
            currentUser.let { user ->
                val matchList = matchListState.matchList

                val filteredMatchList = matchList.filter { matchData ->
                    matchData.id !in chatList.map { it.recipientId }
                }

                val newMatchesSize = matchList.count { it.new }

                SearchBar(
                    value = searchValue,
                    onValueChange = homeViewModel::onSearchChange,
                    placeHolder = "Search matches"
                )

                if (filteredMatchList.any { it.new }) {
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = "New Matches ($newMatchesSize)",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = outfitFamily
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                }

                LazyRow {
                    items(filteredMatchList) { match ->
                        match.let {
                            val matchedUser =
                                matchListState.matchList.find { it.id == match.id }

                            matchedUser?.let {
                                Home_MatchUserItem(
                                    matchedUserData = matchedUser,
                                    new = match.new,
                                    navController = navController,
                                    homeViewModel = homeViewModel,
                                    chatScreenViewModel = chatScreenViewModel
                                )
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))

                LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    val filteredChatList = chatList.filter { chat ->
                        chat.recipientUsername.contains(
                            searchValue,
                            ignoreCase = true
                        )
                    }
                    item {
                        if (matchListState.matchList.isEmpty() && chatListState.chatList.isEmpty()) {
                            EmptyChatAlert()
                        }

                        else if (filteredChatList.isEmpty() && chatList.isNotEmpty()){
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "No one is found.", fontFamily = outfitFamily)
                            }
                        }
                    }

                    items(filteredChatList.sortedByDescending { it.lastUpdated }) { chatData ->
                        val recipientId = chatData.recipientId

                        val recipientIsTyping =
                            if (chatState.recipientUserId == recipientId) chatState.recipientIsTyping
                                ?: false else false


//                        val lastMessage = chatState.lastMessages.find {
//                            it.recipientUserId == user.value?.id && it.senderUserId == recipientId ||
//                                    it.senderUserId == user.value?.id && it.recipientUserId == recipientId
//                        }

                        val lastMessage = chatState.recipientMessages?.find {
                            it.recipientUserId == user.value?.id && it.senderUserId == recipientId ||
                                    it.senderUserId == user.value?.id && it.recipientUserId == recipientId
                        }

                        val lastMessageIsMe = lastMessage?.senderUserId == currentUserId

                        val newMessage =
                            !lastMessageIsMe && lastMessage?.status != MessageStatus.READ && lastMessage != null

                        val lastMessageTime = lastMessage?.timestamp?.toLong() ?: 0
                        val lastMessageIsGif = lastMessage?.text?.endsWith(".gif") ?: false && lastMessage?.text?.contains("media.tenor.com")  ?: false
                        val lastMessageText =
                            if (lastMessageIsGif) "GIF" else lastMessage?.text ?: ""
                        val lastMessageStatus = lastMessage?.status ?: MessageStatus.SENT
                        val lastMessageEmote = lastMessage?.emoted ?: listOf()

                        HomeChatItem(
                            chatData = chatData,
                            lastMessage = lastMessageText,
                            lastMessageStatus = lastMessageStatus,
                            lastMessageSenderIsCurrent = lastMessageIsMe,
                            lastMessageTime = lastMessageTime,
                            newMessage = newMessage,
//                                newMessagesSize = newMessagesSize,
                            typing = recipientIsTyping,
                            lastMessageEmote = lastMessageEmote
                        ) {
                            navController.navigate(Screen.ChatScreen.createRoute(recipientId = chatData.recipientId))
                            homeViewModel.switchRecipient(chatData.recipientId)
                            chatScreenViewModel.switchRecipientInState(chatData.recipientId)
                        }
                    }
                }
            }
        }

    }
}





