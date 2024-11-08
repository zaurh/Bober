package com.zaurh.bober.screen.chat

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.components.ChatBottomBar
import com.zaurh.bober.screen.chat.components.Chat_Content
import com.zaurh.bober.screen.chat.components.Chat_TopBar
import com.zaurh.bober.screen.chat.modal_sheets.Chat_ModalSheet
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.profile.components.BlockAlert
import com.zaurh.bober.screen.profile.components.Profile_ReportAlert
import com.zaurh.bober.screen.profile.components.UnMatchAlert

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatScreen(
    navController: NavController,
    recipientId: String,
    chatScreenViewModel: ChatScreenViewModel,
    profileViewModel: ProfileViewModel,
    blockedUsersViewModel: BlockedUsersViewModel
) {
    Profile_ReportAlert(profileViewModel = profileViewModel)
    BlockAlert(profileViewModel = profileViewModel, navController = navController, blockedUsersViewModel = blockedUsersViewModel)
    UnMatchAlert(profileViewModel = profileViewModel,navController = navController)

    val profileDataState = chatScreenViewModel.profileDataState.collectAsState()
    val currentUserState = chatScreenViewModel.userDataState.collectAsState()

    val userId = currentUserState.value?.id ?: ""

    var username by remember { mutableStateOf("") }
    val messageList = chatScreenViewModel.chatState.value.recipientMessages
    var unReadMessages by remember { mutableStateOf<List<MessageData>>(listOf()) }

    val chatState = chatScreenViewModel.chatState.value
    val messageBarState = rememberMessageBarState()


    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        chatScreenViewModel.onRouteChange(
            onChatScreen = false,
            recipientId = userId
        )
        chatScreenViewModel.clearRecipientMessages()
    }

//    LaunchedEffect(true) {
//        chatScreenViewModel.loadChatData(recipientId)
//        chatScreenViewModel.onRouteChange(
//            onChatScreen = true,
//            recipientId = userId
//        )
//    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        chatScreenViewModel.loadChatData(recipientId)
        chatScreenViewModel.onRouteChange(
            onChatScreen = true,
            recipientId = userId
        )
        unReadMessages = messageList?.filter {
            it.senderUserId == recipientId && it.recipientUserId == userId && it.status != MessageStatus.READ
        } ?: listOf()

        if (unReadMessages.isNotEmpty()) {
            chatScreenViewModel.messageIsRead()
        }
    }



    LaunchedEffect(profileDataState.value) {
        if (profileDataState.value != null) {
            username = profileDataState.value?.username ?: ""
        }
    }

    LaunchedEffect(chatState.recipientDataError) {
        if (chatState.recipientDataError) {
            messageBarState.addError(Exception("Connection failed. Please check your network connection."))
        }
    }


    LaunchedEffect(messageList) {
        unReadMessages = messageList?.filter {
            it.senderUserId == recipientId && it.recipientUserId == userId && it.status != MessageStatus.READ
        } ?: listOf()

        if (unReadMessages.isNotEmpty()) {
            chatScreenViewModel.messageIsRead()
        }
    }


    BackHandler(
        onBack = {
            if (chatScreenViewModel.gifState.value) {
                chatScreenViewModel.onGifStateChange()
            } else {
                navController.popBackStack()
                chatScreenViewModel.clearProfileData()
            }
        }
    )

    Chat_ModalSheet(
        chatScreenViewModel = chatScreenViewModel,
        navController = navController,
        profileId = recipientId,
        blockedUsersViewModel = blockedUsersViewModel,
        profileViewModel = profileViewModel
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
                        navController.navigate(Screen.ProfileScreen.createRoute(recipientId))
                    }
                )
            },

            content = { paddingValues ->
                val currentUser = currentUserState.value ?: UserData()
                ContentWithMessageBar(
                    modifier = Modifier.padding(paddingValues),
                    messageBarState = messageBarState,
                    errorMaxLines = 3,
                    successMaxLines = 3,
                    showCopyButton = false,
                    content = {
                        Chat_Content(
                            chatScreenViewModel = chatScreenViewModel,
                            currentUser = currentUser
                        )
                    })

            },
            bottomBar = {
                ChatBottomBar(
                    chatScreenViewModel = chatScreenViewModel,
                    enabled = true
                )
            }

        )
    }


}



