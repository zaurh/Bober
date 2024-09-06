package com.zaurh.bober.screen.edit_profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.edit_profile.components.EP_Content
import com.zaurh.bober.screen.edit_profile.components.EP_TopBar
import com.zaurh.bober.screen.edit_profile.components.EP_UsernameChange
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_DatePickerMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_GenderMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_HeightMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_InterestsMS
import com.zaurh.bober.screen.edit_profile.components.modal_sheets.EP_LanguageMS

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfileScreen(
    navController: NavController,
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            EP_TopBar(
                navController = navController,
                editProfileViewModel = editProfileViewModel
            )
        }, content = { padding ->
            EP_Content(padding = padding, editProfileViewModel = editProfileViewModel, navController = navController)
        }
    )

    EP_DatePickerMS(editProfileViewModel = editProfileViewModel)
    EP_HeightMS(editProfileViewModel = editProfileViewModel)
    EP_LanguageMS(editProfileViewModel = editProfileViewModel)
    EP_GenderMS(editProfileViewModel = editProfileViewModel)
    EP_UsernameChange(editProfileViewModel = editProfileViewModel)
    EP_InterestsMS(editProfileViewModel = editProfileViewModel)

}








