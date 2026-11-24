package com.zaurh.bober.screen.sign_in

import androidx.compose.material3.CircularProgressIndicator
import com.zaurh.bober.screen.password.PasswordViewModel
import com.zaurh.bober.screen.password.ForgotPasswordDialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.BoberButtonStyle
import com.zaurh.bober.ui.components.BoberPasswordField
import com.zaurh.bober.ui.components.BoberTextField
import com.zaurh.bober.ui.components.GradientBackground
import com.zaurh.bober.ui.theme.BrandFont
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel,
    passwordViewModel: PasswordViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focus = LocalFocusManager.current
    val signedIn = signInViewModel.signedInState.collectAsState()
    val loading = signInViewModel.signInLoading.value || signedIn.value

    LaunchedEffect(signedIn.value) {
        if (signedIn.value) {
            navController.navigate(Screen.PagerScreen.route) { popUpTo(0) }
        }
    }

    fun submit() {
        focus.clearFocus()
        signInViewModel.signIn(context)
    }

    ForgotPasswordDialog(viewModel = passwordViewModel)

    GradientBackground {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))
            Icon(
                painterResource(R.drawable.bober_ic),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Bober", color = Color.White, style = TextStyle(fontFamily = BrandFont, fontSize = 44.sp))
            Spacer(Modifier.height(4.dp))
            Text(
                "Find your people nearby",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(40.dp))

            BoberTextField(
                value = signInViewModel.usernameState.value,
                onValueChange = signInViewModel::onUsernameChange,
                placeholder = "Username",
                onGradient = true,
                imeAction = ImeAction.Next,
                leading = { Icon(painterResource(R.drawable.profile_ic), null, Modifier.size(22.dp)) },
            )
            Spacer(Modifier.height(12.dp))
            BoberPasswordField(
                value = signInViewModel.passwordState.value,
                onValueChange = signInViewModel::onPasswordChange,
                onGradient = true,
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = passwordViewModel::openForgot) {
                    Text("Forgot password?", color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(8.dp))
            BoberButton(
                text = "Sign in",
                onClick = ::submit,
                style = BoberButtonStyle.OnGradient,
                loading = loading,
                enabled = signInViewModel.usernameState.value.isNotBlank() && signInViewModel.passwordState.value.isNotBlank(),
            )

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = 0.4f))
                Text("or", color = Color.White.copy(alpha = 0.85f), modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = 0.4f))
            }
            Spacer(Modifier.height(24.dp))

            Surface(
                onClick = { signInViewModel.signInWithGoogle(context) },
                enabled = !signInViewModel.googleLoading.value,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.18f),
                contentColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (signInViewModel.googleLoading.value) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Image(painterResource(R.drawable.google_logo), contentDescription = null)
                        }
                    }
                    Spacer(Modifier.size(12.dp))
                    Text("Continue with Google", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("New to Bober?", color = Color.White.copy(alpha = 0.85f))
                TextButton(onClick = { navController.navigate(Screen.SignUpUsername.route) }) {
                    Text("Create account", color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
