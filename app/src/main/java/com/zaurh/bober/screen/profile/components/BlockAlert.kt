package com.zaurh.bober.screen.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.profile.ProfileViewModel

@Composable
fun BlockAlert(
    blockedUsersViewModel: BlockedUsersViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    val profileState = profileViewModel.profileDataState.collectAsState()
    val profileUsername = profileState.value?.username ?: ""
    val profileId = profileState.value?.id ?: ""
    val blockAlertState = blockedUsersViewModel.blockAlertState.value

    Profile_ConfirmationAlert(
        title = "Block $profileUsername?",
        text = "Are you sure you want to block $profileUsername?",
        confirmText = "Yes, block.",
        alertState = blockAlertState,
        onDismiss = blockedUsersViewModel::onBlockStateChange
    ) {
        blockedUsersViewModel.blockUser(
            recipientId = profileId,
            onSuccess = {
                navController.popBackStack(
                    route = Screen.ChatScreen.route,
                    inclusive = true
                )
                navController.popBackStack(
                    route = Screen.ProfileScreen.route,
                    inclusive = true
                )
            }
        )
    }
}