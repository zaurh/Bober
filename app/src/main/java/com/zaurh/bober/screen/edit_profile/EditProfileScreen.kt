package com.zaurh.bober.screen.edit_profile

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.edit_profile.components.DiscardChangesAlert
import com.zaurh.bober.screen.edit_profile.components.EP_Content
import com.zaurh.bober.screen.edit_profile.components.EP_TopBar
import com.zaurh.bober.screen.edit_profile.components.EP_UsernameChange
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_DatePickerMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_GenderMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_HeightMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_InterestsMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_LanguageMS

@Composable
fun EditProfileScreen(
    navController: NavController,
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {
    // Both the top bar arrow and the system back ask before dropping unsaved changes.
    fun back() {
        if (editProfileViewModel.discardChanges.value) editProfileViewModel.onDiscardChangesAlert()
        else navController.popBackStack()
    }
    BackHandler(onBack = ::back)

    Scaffold(
        topBar = {
            EP_TopBar(
                editProfileViewModel = editProfileViewModel,
                onBack = ::back,
                onSaved = { navController.popBackStack() }
            )
        }
    ) { padding ->
        EP_Content(padding = padding, editProfileViewModel = editProfileViewModel)
    }

    DiscardChangesAlert(
        alertState = editProfileViewModel.discardChangesAlert.value,
        onDismiss = editProfileViewModel::onDiscardChangesAlert,
        onDiscard = {
            editProfileViewModel.onDiscardChangesAlert()
            navController.popBackStack()
        }
    )
    EP_DatePickerMS(editProfileViewModel = editProfileViewModel)
    EP_HeightMS(editProfileViewModel = editProfileViewModel)
    EP_LanguageMS(editProfileViewModel = editProfileViewModel)
    EP_GenderMS(editProfileViewModel = editProfileViewModel)
    EP_UsernameChange(editProfileViewModel = editProfileViewModel)
    EP_InterestsMS(editProfileViewModel = editProfileViewModel)
}
