package com.zaurh.bober.screen.settings.account_center.email

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.settings.account_center.email.components.Email_Content
import com.zaurh.bober.screen.settings.account_center.email.components.Email_TopBar

@Composable
fun EmailScreen(
    token: String,
    navController: NavController,
    emailScreenViewModel: EmailScreenViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            Email_TopBar(navController)
        },
        content = { paddingValues ->
            Email_Content(
                token = token,
                emailScreenViewModel = emailScreenViewModel,
                paddingValues = paddingValues
            )
        }
    )
}