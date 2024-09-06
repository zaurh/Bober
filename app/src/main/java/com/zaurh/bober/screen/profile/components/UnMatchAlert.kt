package com.zaurh.bober.screen.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.profile.ProfileViewModel

@Composable
fun UnMatchAlert(
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    val profileState = profileViewModel.profileDataState.collectAsState()
    val profileUsername = profileState.value?.username ?: ""
    val profileId = profileState.value?.id ?: ""
    val unmatchAlertState = profileViewModel.unmatchAlertState.value

    Profile_ConfirmationAlert(
        title = "Unmatch $profileUsername?",
        text = "Are you sure you want to unmatch with $profileUsername?",
        confirmText = "Yes, unmatch.",
        alertState = unmatchAlertState,
        onDismiss = profileViewModel::onUnMatchStateChange
    ) {
        profileViewModel.unMatch(
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