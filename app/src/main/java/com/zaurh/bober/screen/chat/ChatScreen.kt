package com.zaurh.bober.screen.chat

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.chat.components.ChatBottomBar
import com.zaurh.bober.screen.chat.components.Chat_Content
import com.zaurh.bober.screen.chat.components.Chat_TopBar
import com.zaurh.bober.screen.chat.modal_sheets.Chat_BackgroundSheet
import com.zaurh.bober.screen.chat.modal_sheets.Chat_MessageActionsSheet
import com.zaurh.bober.screen.chat.modal_sheets.Chat_ModalSheet
import com.zaurh.bober.screen.chat.modal_sheets.Chat_ReactionsSheet
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.profile.components.ProfileDialogs

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatScreen(
    navController: NavController,
    username: String,
    chatScreenViewModel: ChatScreenViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileDataState = chatScreenViewModel.profileDataState.collectAsState()
    val currentUserState = chatScreenViewModel.userDataState.collectAsState()


    val userId = currentUserState.value?.id ?: ""
    val profileId = profileDataState.value?.id
    val messageList = chatScreenViewModel.state.value.messages


    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        chatScreenViewModel.clearProfileData()
        chatScreenViewModel.onRouteChange(
            onChatScreen = false,
            recipientId = ""
        )
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        chatScreenViewModel.getProfileData(username)
    }

    // The backend routes messages, typing and read receipts to the selected recipient, so select the
    // chat partner however this screen was opened (chat list, profile, notification...).
    LaunchedEffect(profileId) {
        if (profileId != null) {
            chatScreenViewModel.switchRecipient(profileId)
            chatScreenViewModel.onRouteChange(onChatScreen = true, recipientId = profileId)
        }
    }

    LaunchedEffect(messageList, profileId) {
        val hasUnread = messageList.any {
            it.senderUserId == profileId && it.recipientUserId == userId && it.status != MessageStatus.READ
        }
        // One receipt marks the whole conversation as read on the server.
        if (profileId != null && hasUnread) chatScreenViewModel.markMessagesRead()
    }


    BackHandler(
        onBack = {
            if (chatScreenViewModel.gifState.value) {
                chatScreenViewModel.onGifStateChange()
            } else if (chatScreenViewModel.replyingTo.value != null) {
                chatScreenViewModel.cancelReply()
            } else {
                navController.popBackStack()
                chatScreenViewModel.clearProfileData()
            }
        }
    )

    Chat_ModalSheet(
        chatScreenViewModel = chatScreenViewModel,
        onViewProfile = { navController.navigate(Screen.ProfileScreen.passUsername(username)) },
        profileViewModel = profileViewModel
    )

    Chat_MessageActionsSheet(chatScreenViewModel = chatScreenViewModel, currentUserId = userId)
    Chat_ReactionsSheet(chatScreenViewModel = chatScreenViewModel, currentUserId = userId)
    val chatList = chatScreenViewModel.chatList.collectAsState().value
    Chat_BackgroundSheet(
        chatScreenViewModel = chatScreenViewModel,
        chat = chatList.firstOrNull { it?.recipientId == profileId }
    )

    ProfileDialogs(
        viewModel = profileViewModel,
        user = profileDataState.value,
        onRelationshipEnded = {
            chatScreenViewModel.clearProfileData()
            navController.popBackStack(Screen.PagerScreen.route, inclusive = false)
        }
    )

    Box {
        Scaffold(
            topBar = {
                val profilePic = profileDataState.value?.imageUrl?.firstOrNull() ?: ""

                Chat_TopBar(
                    image = profilePic,
                    username = username,
                    navController = navController,
                    chatScreenViewModel = chatScreenViewModel,
                    onClick = {
                        navController.navigate(Screen.ProfileScreen.passUsername(username))
                    }
                )
            },
            modifier = Modifier
                .fillMaxSize(),
            content = { paddingValues ->
                val userData = profileDataState.value ?: UserData()
                val currentUser = currentUserState.value ?: UserData()

                Chat_Content(
                    chatScreenViewModel = chatScreenViewModel,
                    userData = userData,
                    currentUser = currentUser,
                    paddingValues = paddingValues
                )
            },
            bottomBar = {
                ChatBottomBar(chatScreenViewModel = chatScreenViewModel)
            }

        )
    }

}



