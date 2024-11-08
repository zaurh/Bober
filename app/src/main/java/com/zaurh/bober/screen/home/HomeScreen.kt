package com.zaurh.bober.screen.home

import android.app.Activity
import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
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
    homeViewModel: HomeViewModel = hiltViewModel(),
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
        if (chatList.isEmpty()){
            chatScreenViewModel.getChatList()
        }
        if (matchListState.matchList.isEmpty()){
            chatScreenViewModel.getMatchList()
        }
        if (chatState.lastMessages.isEmpty()){
            Log.d("dkaldjas", "SDakljdsa")
            chatScreenViewModel.getLastMessages()
        }
        homeViewModel.storeFCMToken()
        chatScreenViewModel.onRouteChange(
            onChatScreen = false,
            recipientId = ""
        )
        chatScreenViewModel.clearRecipientMessages()
        chatScreenViewModel.clearProfileData()
        chatScreenViewModel.clearEmptyMessages()
    }



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
                        color = MaterialTheme.colorScheme.primary
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

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val filteredChatList = chatList.filter { chat ->
                        chat.recipientUsername.contains(
                            searchValue,
                            ignoreCase = true
                        )
                    }
                    item {
                        if (matchList.isEmpty()) {
                            EmptyChatAlert()
                        } else if (filteredChatList.isEmpty() && chatList.isNotEmpty()){
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "No one is found.")
                            }
                        }
                    }

                    items(filteredChatList) { chatData ->
                        val recipientId = chatData.recipientId

                        val recipientIsTyping =
                            if (chatState.recipientUserId == recipientId) chatState.recipientIsTyping
                                ?: false else false


                        val lastMessage = chatState.lastMessages.find {
                            it.recipientUserId == user.value?.id && it.senderUserId == recipientId ||
                                    it.senderUserId == user.value?.id && it.recipientUserId == recipientId
                        }


                        val lastMessageIsMe = lastMessage?.senderUserId == currentUserId

                        val newMessage =
                            !lastMessageIsMe && lastMessage?.messageStatus != MessageStatus.READ && lastMessage != null

                        val lastMessageTime = lastMessage?.messageTime?.toLong() ?: 0
                        val lastMessageIsGif = lastMessage?.messageText?.endsWith(".gif") ?: false
                        val lastMessageText =
                            if (lastMessageIsGif) "GIF" else lastMessage?.messageText ?: ""
                        val lastMessageStatus = lastMessage?.messageStatus ?: MessageStatus.SENT

                        HomeChatItem(
                            chatData = chatData,
                            lastMessage = lastMessageText,
                            lastMessageStatus = lastMessageStatus,
                            lastMessageSenderIsCurrent = lastMessageIsMe,
                            lastMessageTime = lastMessageTime,
                            newMessage = newMessage,
//                                newMessagesSize = newMessagesSize,
                            typing = recipientIsTyping
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





