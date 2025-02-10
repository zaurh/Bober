package com.zaurh.bober.screen.settings.account_center.email.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import com.zaurh.bober.screen.settings.account_center.components.AC_TextField
import com.zaurh.bober.screen.settings.account_center.email.EmailScreenViewModel
import com.zaurh.bober.screen.settings.components.SettingsDivider
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun Email_Content(
    token: String,
    emailScreenViewModel: EmailScreenViewModel,
    paddingValues: PaddingValues
) {
    val state = rememberMessageBarState()
    val currentUser = emailScreenViewModel.userDataState.collectAsState()

    val emailIsVerified = currentUser.value?.emailIsVerified ?: false
    val emailText = emailScreenViewModel.emailText.value
    val emailVerificationState = emailScreenViewModel.emailVerificationState.value
    val emailValidationState = emailScreenViewModel.emailValidationState.value

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        emailScreenViewModel.getUserData()
        if (token.isNotEmpty()) {
            emailScreenViewModel.validateEmailToken(token = token)
        }
    }

    LaunchedEffect(emailVerificationState.message) {
        if (emailVerificationState.message.isNotEmpty()) {
            if (emailVerificationState.success) {
                state.addSuccess(
                    message = emailVerificationState.message
                )
            } else {
                state.addError(
                    exception = Exception(emailVerificationState.message)
                )
            }
        }
    }

    LaunchedEffect(emailValidationState.message) {
        if (emailValidationState.message.isNotEmpty()) {
            if (emailValidationState.success) {
                state.addSuccess(
                    message = emailValidationState.message
                )
            } else {
                state.addError(
                    exception = Exception(emailValidationState.message)
                )
            }
        }
    }

    ContentWithMessageBar(
        modifier = Modifier.padding(paddingValues),
        verticalPadding = 6.dp,
        messageBarState = state,
        errorMaxLines = 3,
        successMaxLines = 3,
        showCopyButton = false,
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Verifying your email ensures that you can provide feedback," +
                                " reach out to us with any issues, and securely reset your password if needed.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = outfitFamily
                    )
                    SettingsDivider()
                    AC_TextField(
                        title = "Email",
                        placeholder = "Enter your email address",
                        value = emailText,
                        onValueChange = emailScreenViewModel::onEmailChange,
                        enabled = !emailIsVerified,
                        singleLine = true,
                        keyboardType = KeyboardType.Email,
                        visualTransformation = VisualTransformation.None
                    )
                    Spacer(Modifier.size(8.dp))
                    Button(enabled = !emailIsVerified && !emailVerificationState.loading && !emailValidationState.loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                            emailScreenViewModel.sendEmailVerification()
                        }) {
                        if (emailVerificationState.loading) {
                            Text(text = "Loading...", color = Color.White, fontFamily = outfitFamily, fontSize = 16.sp)
                            Spacer(modifier = Modifier.size(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (emailIsVerified) "Verified" else "Verify",
                                color = Color.White,
                                fontFamily = outfitFamily,
                                fontSize = 16.sp
                            )
                        }
                    }

                }

            }

        }
    )


}