package com.zaurh.bober.screen.sign_up.sign_up_username

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.ui.components.AuthScaffold
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.BoberButtonStyle
import com.zaurh.bober.ui.components.BoberTextField
import androidx.compose.material3.MaterialTheme

@Composable
fun SignUpUsername(
    signUpViewModel: SignUpViewModel,
    navController: NavController
) {
    val state = signUpViewModel.usernameState.value
    val focus = LocalFocusManager.current

    fun next() {
        signUpViewModel.checkUsername {
            focus.clearFocus()
            navController.navigate(Screen.SignUpGender.route)
        }
    }

    AuthScaffold(
        title = "Pick a username",
        subtitle = "This is how other bobers will find you. You can't change it later.",
        step = 1,
        onBack = { navController.popBackStack() },
        bottom = {
            BoberButton(
                text = "Continue",
                onClick = ::next,
                style = BoberButtonStyle.OnGradient,
                loading = state.loading,
                loadingText = "Checking…",
                enabled = state.username.isNotBlank()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = Color.White.copy(alpha = 0.85f))
                TextButton(onClick = {
                    navController.navigate(Screen.SignInScreen.route) { popUpTo(0) }
                }) { Text("Sign in", color = Color.White, style = MaterialTheme.typography.labelLarge) }
            }
        }
    ) {
        BoberTextField(
            value = state.username,
            onValueChange = signUpViewModel::onUsernameChange,
            placeholder = "username",
            onGradient = true,
            error = state.errorMessage.ifEmpty { null },
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = { next() }),
            leading = { Text("@", fontWeight = FontWeight.Bold) },
        )
    }
}
