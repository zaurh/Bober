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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.home.components.Home_MatchUserItem
import com.zaurh.bober.screen.home.components.SearchBar

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    chatScreenViewModel: ChatScreenViewModel
) {

    val messageList = chatScreenViewModel.state.value.messages
    val currentUser = homeViewModel.userDataState.collectAsState()
    val currentUserId = currentUser.value?.id ?: ""
    val userListDataState = homeViewModel.userListDataState.collectAsState()
    val searchValue = homeViewModel.searchQuery.value
    val context = LocalContext.current
    val chatState = chatScreenViewModel.state.value


    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        homeViewModel.getAllUsers()
        homeViewModel.getUserData()
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
            val matchList = user.value?.matchList?.reversed()
            val chatList = user.value?.chatList

            val filteredChatList = user.value?.chatList?.sortedByDescending { it.lastUpdated }?.filter {
                chat ->
                chat.username.contains(searchValue, ignoreCase = true)
            } ?: listOf()

            val filteredMatchList = matchList?.filter { matchData ->
                matchData.matchUserId !in (chatList?.map { it.userId } ?: listOf())
            } ?: listOf()

            val newMatchesSize = matchList?.count { it.new } ?: ""

            SearchBar(
                value = searchValue,
                onValueChange = homeViewModel::onSearchChange,
                placeHolder = "Search matches"
            )

            if (filteredMatchList.any { it.new }){
                Spacer(modifier = Modifier.size(12.dp))
                Text(text = "New Matches ($newMatchesSize)", color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.size(12.dp))
            }

            LazyRow {
                items(filteredMatchList) { match ->
                    match.let {
                        val matchedUser =
                            userListDataState.value.find { it?.id == match.matchUserId }

                        Home_MatchUserItem(
                            userData = matchedUser ?: UserData(),
                            new = match.new,
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            LazyColumn {
                item {
                    if (matchList?.isEmpty() == true){
                        Text("You don't have any match yet.")
                    }
                }
                items(filteredChatList.map { it.userId }) { chatUserId ->
                    val filteredList = messageList.filter {
                        it.senderUserId == currentUser.value?.id && it.recipientUserId == chatUserId
                                || it.senderUserId == chatUserId && it.recipientUserId == currentUser.value?.id
                    }.reversed()

                    val recipientIsTyping =
                        if (chatState.recipientUserId == chatUserId) chatState.recipientIsTyping
                            ?: false else false

                    val lastMessage = filteredList.lastOrNull()
                    val lastMessageTime = lastMessage?.timestamp
                    val lastMessageIsMe = lastMessage?.senderUserId == currentUserId
                    val lastMessageIsGif = lastMessage?.text?.endsWith(".gif") ?: false
                    val lastMessageText = if (lastMessageIsGif) "GIF" else lastMessage?.text ?: ""
                    val lastMessageStatus = lastMessage?.status ?: MessageStatus.SENT

                    val unReadMessages = filteredList.filter { it.recipientUserId == currentUserId && it.status != MessageStatus.READ }
                    val newMessagesSize = unReadMessages.size

                    val newMessage =
                        !lastMessageIsMe && lastMessage?.status != MessageStatus.READ && lastMessage != null

                    val userData =
                        userListDataState.value.find { it?.id == chatUserId } ?: UserData()

                    MatchUserItem(
                        userData = userData,
                        lastMessage = lastMessageText,
                        lastMessageTime = lastMessageTime ?: "",
                        lastMessageSenderIsCurrent = lastMessageIsMe,
                        lastMessageStatus = lastMessageStatus,
                        newMessage = newMessage,
                        newMessagesSize = newMessagesSize,
                        typing = recipientIsTyping
                    ) {
                        navController.navigate(Screen.ChatScreen.createRoute(username = userData.username))
                    }
                }
            }
        }
    }
}





