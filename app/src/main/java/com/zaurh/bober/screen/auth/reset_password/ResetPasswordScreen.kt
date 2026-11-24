package com.zaurh.bober.screen.auth.reset_password

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.auth.reset_password.components.RP_Content
import com.zaurh.bober.screen.auth.reset_password.components.RP_TopBar

@Composable
fun ResetPasswordScreen(
    id: String,
    token: String,
    navController: NavController,
    resetPasswordViewModel: ResetPasswordViewModel
) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        resetPasswordViewModel.validateToken(token = token)
    }

//    val signedIn = resetPasswordViewModel.signedInState.collectAsState()
//
//    if (signedIn.value) {
//        navController.navigate(Screen.PagerScreen.route)
//    }

    Scaffold(
        topBar = {
            RP_TopBar(
                onIconClick = {
                    navController.popBackStack()
                }
            )
        },
        content = { paddingValues ->
            RP_Content(
                id = id,
                paddingValues = paddingValues,
                resetPasswordViewModel = resetPasswordViewModel,
                onSuccess = {
                    navController.navigate(Screen.PagerScreen.route)
                }
            )

        }
    )


}





