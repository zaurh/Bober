package com.zaurh.bober.screen.chat

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.components.ChatBottomBar
import com.zaurh.bober.screen.chat.components.Chat_Content
import com.zaurh.bober.screen.chat.components.Chat_TopBar
import com.zaurh.bober.screen.chat.modal_sheets.Chat_BackgroundModalSheet
import com.zaurh.bober.screen.chat.modal_sheets.Chat_EmoteModalSheet
import com.zaurh.bober.screen.chat.modal_sheets.Chat_ModalSheet
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.profile.components.BlockAlert
import com.zaurh.bober.screen.profile.components.Profile_ReportAlert
import com.zaurh.bober.screen.profile.components.UnMatchAlert

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

    val matchData = chatScreenViewModel.matchListState.value.matchList.find { it.id == recipientId }
    val chatData = chatScreenViewModel.chatListState.value.chatList.find { it.recipientId == recipientId }
    val currentUserState = chatScreenViewModel.userDataState.collectAsState()

    val userId = currentUserState.value?.id ?: ""

    val chatState = chatScreenViewModel.chatState.value
    val messageBarState = rememberMessageBarState()
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current


    LaunchedEffect(chatScreenViewModel.replyingState.value.enabled) {
        if (chatScreenViewModel.replyingState.value.enabled){
            focusManager.clearFocus()
            focusRequester.requestFocus()
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        chatScreenViewModel.onRouteChange(
            onChatScreen = false,
            recipientId = userId
        )
        chatScreenViewModel.closeReply()
    }

    LaunchedEffect(true) {
        chatScreenViewModel.getPrivMessages(recipientId)
        chatScreenViewModel.getProfileData(recipientId)
        chatScreenViewModel.onRouteChange(
            onChatScreen = true,
            recipientId = userId
        )
        Log.d("sadkjsdalksd", "asdjkhaskhsdakj")

    }


    LaunchedEffect(chatState.recipientMessages) {
        Log.d("sadkjsdalksd", "${chatState.recipientMessages}")
    }


    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
       chatScreenViewModel.handleDeepLink()
        chatScreenViewModel.onRouteChange(
            onChatScreen = true,
            recipientId = userId
        )
    }


    LaunchedEffect(chatState.recipientDataError) {
        if (chatState.recipientDataError) {
            messageBarState.addError(Exception("Connection failed. Please check your network connection."))
        }
    }



    BackHandler(
        onBack = {
            if (chatScreenViewModel.gifState.value) {
                chatScreenViewModel.onGifStateChange()
            }
            else if (chatScreenViewModel.replyingState.value.enabled){
                chatScreenViewModel.closeReply()
            }
            else if(chatScreenViewModel.selectedMessages.value.isNotEmpty()){
                chatScreenViewModel.clearSelectMessages()
            }
            else {
                navController.popBackStack()
                chatScreenViewModel.clearProfileData()
            }
        }
    )

    Chat_ModalSheet(
        chatScreenViewModel = chatScreenViewModel,
        blockedUsersViewModel = blockedUsersViewModel,
        profileViewModel = profileViewModel,
        backgroundEnabled = chatData != null
    )

    Chat_BackgroundModalSheet(
        chatScreenViewModel = chatScreenViewModel,
        recipientId = recipientId,
        doodle = chatData?.doodleEnabled ?: false,
        backgroundColor = chatData?.backgroundColor ?: BackgroundColor.DEFAULT,
    )


    Chat_EmoteModalSheet(
        chatScreenViewModel = chatScreenViewModel,
        enabled = chatScreenViewModel.emoteModalState.value.enabled,
        currentUserId = currentUserState.value?.id ?: "",
        onClick = {
            chatScreenViewModel.closeEmoteModalSheet()
            if (it.senderId == currentUserState.value?.id){
                chatScreenViewModel.removeEmote(
                    senderId = currentUserState.value?.id ?: "",
                    recipientId = recipientId,
                    messageId = chatScreenViewModel.emoteModalState.value.messageData?.id ?: ""
                )
            }else{
                navController.navigate(Screen.ProfileScreen.createRoute(recipientId))
            }
        },
        onDismiss = {
            chatScreenViewModel.closeEmoteModalSheet()
        }
    )




    Box {
        Scaffold(
            topBar = {

                Chat_TopBar(
                    image = chatData?.recipientImage ?: matchData?.image ?: "",
                    username = chatData?.recipientUsername ?: matchData?.username ?: "",
                    navController = navController,
                    chatScreenViewModel = chatScreenViewModel,
                    profileViewModel = profileViewModel,
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
                            currentUser = currentUser,
                            profileId = recipientId,
                            backgroundColor = chatData?.backgroundColor ?: BackgroundColor.DEFAULT,
                            doodleEnabled = chatData?.doodleEnabled ?: true
                        )
                    })

            },
            bottomBar = {
                ChatBottomBar(
                    chatScreenViewModel = chatScreenViewModel,
                    enabled = true,
                    backgroundColor = chatData?.backgroundColor ?: BackgroundColor.DEFAULT,
                    focusRequester = focusRequester
                )
            }

        )
    }


}



