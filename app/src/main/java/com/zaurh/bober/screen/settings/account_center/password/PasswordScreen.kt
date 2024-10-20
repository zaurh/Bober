package com.zaurh.bober.screen.settings.account_center.password

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.settings.account_center.password.components.Password_Content
import com.zaurh.bober.screen.settings.account_center.password.components.Password_TopBar

@Composable
fun PasswordScreen(
    navController: NavController,
    passwordScreenViewModel: PasswordScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            Password_TopBar(navController)
        },
        content = { paddingValues ->
            Password_Content(
                passwordScreenViewModel = passwordScreenViewModel,
                paddingValues = paddingValues
            )
        }
    )
}