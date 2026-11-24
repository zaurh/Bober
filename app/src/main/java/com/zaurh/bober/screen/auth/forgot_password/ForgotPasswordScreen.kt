package com.zaurh.bober.screen.auth.forgot_password

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.auth.forgot_password.components.FP_Content
import com.zaurh.bober.screen.auth.forgot_password.components.FP_TopBar

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            FP_TopBar(
                onIconClick = {
                    navController.popBackStack()
                }
            )
        },
        content = { paddingValues ->
            FP_Content(
                paddingValues = paddingValues,
                forgotPasswordViewModel = forgotPasswordViewModel
            )

        }
    )
}




