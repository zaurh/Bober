package com.zaurh.bober.screen.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.profile.ProfileViewModel

@Composable
fun BlockAlert(
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    val profileState = profileViewModel.profileDataState.collectAsState()
    val profileUsername = profileState.value?.username ?: ""
    val profileId = profileState.value?.id ?: ""
    val blockAlertState = profileViewModel.blockAlertState.value

    Profile_ConfirmationAlert(
        title = "Block $profileUsername?",
        text = "Are you sure you want to block $profileUsername?",
        confirmText = "Yes, block.",
        alertState = blockAlertState,
        onDismiss = profileViewModel::onBlockStateChange
    ) {
        profileViewModel.block(
            recipientId = profileId,
            onSuccess = {
                navController.popBackStack(
                    route = Screen.ChatScreen.route,
                    inclusive = true
                )
            }
        )
    }
}