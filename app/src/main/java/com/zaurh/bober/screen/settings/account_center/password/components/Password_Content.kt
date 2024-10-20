package com.zaurh.bober.screen.settings.account_center.password.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarPosition
import com.stevdzasan.messagebar.rememberMessageBarState
import com.zaurh.bober.R
import com.zaurh.bober.screen.settings.account_center.components.AC_TextField
import com.zaurh.bober.screen.settings.account_center.password.PasswordScreenViewModel
import com.zaurh.bober.screen.settings.components.SettingsDivider

@Composable
fun Password_Content(
    passwordScreenViewModel: PasswordScreenViewModel,
    paddingValues: PaddingValues
) {
    val state = rememberMessageBarState()
    val currentUser = passwordScreenViewModel.userDataState.collectAsState()
    val passwordState = passwordScreenViewModel.passwordState.value

    LaunchedEffect(passwordState.updatePasswordMessage) {
        if (passwordState.updatePasswordMessage.isNotEmpty()) {
            if (passwordState.updatePasswordSuccess) {
                state.addSuccess(
                    message = passwordState.updatePasswordMessage
                )
            } else {
                state.addError(
                    exception = Exception(passwordState.updatePasswordMessage)
                )
            }
        }
    }

    LaunchedEffect(passwordState.resetPasswordMessage) {
        if (passwordState.resetPasswordMessage.isNotEmpty()) {
            if (passwordState.resetPasswordSuccess) {
                state.addSuccess(
                    message = passwordState.resetPasswordMessage
                )
            } else {
                state.addError(
                    exception = Exception(passwordState.resetPasswordMessage)
                )
            }
        }
    }

    ContentWithMessageBar(
        modifier = Modifier.padding(paddingValues),
        verticalPadding = 6.dp,
        messageBarState = state,
        position = MessageBarPosition.TOP,
        errorMaxLines = 3,
        successMaxLines = 3,
        visibilityDuration = 3000,
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
                        "To change your password, you need to enter your current password." +
                                " If you do not remember your current password, click the button below:",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (passwordState.resetPasswordLoading) "Loading..." else "Reset password",
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable(enabled = !passwordState.resetPasswordLoading) {
                            passwordScreenViewModel.resetPassword()
                        }
                    )
                    SettingsDivider()
                    AC_TextField(
                        title = "Current password",
                        placeholder = "Enter your current password",
                        value = passwordState.currentPassword,
                        onValueChange = passwordScreenViewModel::onCurrentPasswordChange,
                        singleLine = true,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordState.currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordState.currentPasswordVisible)
                                painterResource(id = R.drawable.visibilityon_ic)
                            else painterResource(id = R.drawable.visibilityoff_ic)

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = passwordScreenViewModel::onCurrentPasswordVisibilityChange
                            ) {
                                Icon(painter = image, contentDescription = "")
                            }
                        }
                    )
                    Spacer(Modifier.size(8.dp))
                    AC_TextField(
                        title = "New password",
                        placeholder = "Enter new password",
                        value = passwordState.newPassword,
                        onValueChange = passwordScreenViewModel::onNewPasswordChange,
                        singleLine = true,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordState.newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordState.newPasswordVisible)
                                painterResource(id = R.drawable.visibilityon_ic)
                            else painterResource(id = R.drawable.visibilityoff_ic)

                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = passwordScreenViewModel::onNewPasswordVisibilityChange
                            ) {
                                Icon(painter = image, contentDescription = "")
                            }
                        }
                    )
                    Spacer(Modifier.size(8.dp))
                    Button(enabled = !passwordState.updatePasswordLoading,colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary
                    ), onClick = {
                        passwordScreenViewModel.checkCurrentPassword()
                    }) {
                        if (passwordState.updatePasswordLoading) {
                            Text(text = "Loading...", color = Color.White)
                            Spacer(modifier = Modifier.size(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Change password",
                                color = Color.White
                            )
                        }
                    }

                }

            }

        }
    )


}