package com.zaurh.bober.screen.chat

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
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

        chatScreenViewModel.onRouteChange(
            onChatScreen = true,
            recipientId = userId
        )
    }

    LaunchedEffect(key1 = profileId) {
        chatScreenViewModel.switchRecipient(profileId ?: "")
    }
    LaunchedEffect(messageList, profileId) {

        val unreadMessages = messageList.filter {
            it.senderUserId == profileId && it.recipientUserId == userId && it.status != MessageStatus.READ
        }
        unreadMessages.forEach {
            chatScreenViewModel.messageIsRead(it.id ?: "")
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
        profileId = profileId ?: ""
    )

    Profile_ReportAlert(profileViewModel = profileViewModel)
    BlockAlert(profileViewModel = profileViewModel, navController = navController)
    UnMatchAlert(profileViewModel = profileViewModel, navController = navController)

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
                Image(
                    painter = painterResource(id = R.drawable.doodle),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.3f),
                    contentScale = ContentScale.Crop
                )
                Chat_Content(
                    chatScreenViewModel = chatScreenViewModel,
                    userData = userData,
                    currentUser = currentUser,
                    paddingValues = paddingValues
                )
            },
            bottomBar = {
                ChatBottomBar(
                    chatScreenViewModel = chatScreenViewModel
                )
            }

        )
    }

}



