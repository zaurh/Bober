package com.zaurh.bober.screen.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.profile.components.ProfileContent
import com.zaurh.bober.screen.profile.components.Profile_TopBar
import com.zaurh.bober.util.calculateAge

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    recipientId: String,
    profileViewModel: ProfileViewModel,
    chatScreenViewModel: ChatScreenViewModel,
    blockedUsersViewModel: BlockedUsersViewModel,
    navController: NavController
) {
//    val profileData = chatScreenViewModel.chatListState.value.chatList.find { it.recipientId == recipientId }
    val profileData = profileViewModel.profileDataState.collectAsState()
    var username by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        profileViewModel.getProfileID(recipientId)
    }

    LaunchedEffect(profileData.value) {
        if (profileData.value != null) {
            username = profileData.value?.username ?: ""
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        profileViewModel.cancelGetProfileJob()
        profileViewModel.clearProfileData()
    }


    Surface(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surface)) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                val age = calculateAge(profileData.value?.birthDate ?: "").toString()

                Profile_TopBar(
                    navController = navController,
                    username = username,
                    age = age,
                    profileViewModel = profileViewModel
                )
            },
            content = { paddingValues ->
                profileData.value.let { user ->
                    if (user != null) {
                        ProfileContent(
                            paddingValues = paddingValues,
                            profileViewModel = profileViewModel,
                            chatScreenViewModel = chatScreenViewModel,
                            user = user,
                            navController = navController,
                            blockedUsersViewModel = blockedUsersViewModel
                        )
                    }

                }
            })

    }

}




