package com.zaurh.bober.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.profile.components.ProfileContent
import com.zaurh.bober.screen.profile.components.Profile_TopBar

@Composable
fun ProfileScreen(
    recipientId: String,
    profileViewModel: ProfileViewModel,
    chatScreenViewModel: ChatScreenViewModel,
    blockedUsersViewModel: BlockedUsersViewModel,
    navController: NavController
) {
    val profileData = profileViewModel.profileDataState.collectAsState()

    LaunchedEffect(key1 = true) {
        profileViewModel.getProfileID(recipientId)
    }


    DisposableEffect(key1 = true){
        onDispose {
            profileViewModel.cancelGetProfileJob()
            profileViewModel.clearProfileData()
        }
    }



    Surface(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.surface)) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {

                Profile_TopBar(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            },
            content = { paddingValues ->
                ProfileContent(
                    paddingValues = paddingValues,
                    profileViewModel = profileViewModel,
                    chatScreenViewModel = chatScreenViewModel,
                    profileData = profileData.value,
                    navController = navController,
                    blockedUsersViewModel = blockedUsersViewModel
                )
            })

    }

}




