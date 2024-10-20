package com.zaurh.bober.screen.reset_password.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.screen.reset_password.ResetPasswordViewModel
import com.zaurh.bober.screen.sign_in.components.SignIn_TextField

@Composable
fun RP_Content(
    id: String,
    paddingValues: PaddingValues,
    onSuccess: () -> Unit,
    resetPasswordViewModel: ResetPasswordViewModel
) {
    val passwordState = resetPasswordViewModel.passwordState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(id = R.color.backgroundTop),
                        colorResource(id = R.color.backgroundBottom)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    )
    {
        if (passwordState.validateTokenLoading) {
            CircularProgressIndicator()
        } else if (!passwordState.validationSuccess) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Token is no longer valid. Please try sending new email to reset your password.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Create a new password",
                    color = Color.White,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Enter your new password",
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(Modifier.size(12.dp))
                SignIn_TextField(
                    label = "New password",
                    value = passwordState.newPassword,
                    onValueChange = resetPasswordViewModel::onNewPasswordChange,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordState.newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordState.newPasswordVisible)
                            painterResource(id = R.drawable.visibilityon_ic)
                        else painterResource(id = R.drawable.visibilityoff_ic)

                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = resetPasswordViewModel::onNewPasswordVisibilityChange
                        ) {
                            Icon(painter = image, contentDescription = "", tint = Color.Gray)
                        }
                    }
                )
                Spacer(Modifier.size(8.dp))
                SignIn_TextField(
                    label = "Confirm password",
                    value = passwordState.newPasswordAgain,
                    onValueChange = resetPasswordViewModel::onNewPasswordAgainChange,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordState.newPasswordAgainVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordState.newPasswordAgainVisible)
                            painterResource(id = R.drawable.visibilityon_ic)
                        else painterResource(id = R.drawable.visibilityoff_ic)

                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = resetPasswordViewModel::onNewPasswordAgainVisibilityChange
                        ) {
                            Icon(painter = image, contentDescription = "", tint = Color.Gray)
                        }
                    }
                )
                AnimatedVisibility(passwordState.updatePasswordMessage.isNotEmpty()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            passwordState.updatePasswordMessage,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Button(enabled = !passwordState.updatePasswordLoading,colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                disabledContainerColor = Color.White
            ), onClick = {
                resetPasswordViewModel.updatePassword(
                    userId = id,
                    onSuccess = {
                        onSuccess()
                    }
                )
            }) {
                if (passwordState.updatePasswordLoading) {
                    Text(text = "Loading...", color = colorResource(id = R.color.darkBlue))
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = colorResource(id = R.color.backgroundTop),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Confirm", color = colorResource(id = R.color.darkBlue))
                }
            }


        }


    }

}