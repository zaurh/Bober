package com.zaurh.bober.screen.forgot_password.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.screen.forgot_password.ForgotPasswordViewModel
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun FP_Content(
    paddingValues: PaddingValues,
    forgotPasswordViewModel: ForgotPasswordViewModel
) {
    val fpState = forgotPasswordViewModel.fpState.value

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
        Text(
            "Forgot password?",
            color = Color.White,
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            fontFamily = outfitFamily
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Enter the email associated with your account and we'll send an email with instructions to reset your password.",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = outfitFamily
            )
            Spacer(Modifier.size(12.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20)),
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "", tint = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.darkBlue),
                    unfocusedTextColor = colorResource(id = R.color.darkBlue),
                    focusedTextColor = colorResource(id = R.color.darkBlue),
                    selectionColors = TextSelectionColors(
                        handleColor = colorResource(R.color.backgroundTop),
                        backgroundColor = colorResource(R.color.backgroundTop)
                    )
                ),
                value = forgotPasswordViewModel.fpState.value.emailText,
                onValueChange = forgotPasswordViewModel::onEmailChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                placeholder = { Text(text = "Enter email", color = Color.Gray, fontFamily = outfitFamily) },
                textStyle = TextStyle(
                    fontFamily = outfitFamily,
                    fontSize = 16.sp
                )
            )
            AnimatedVisibility(fpState.message != "") {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(fpState.message, color = Color.White, textAlign = TextAlign.Center, fontFamily = outfitFamily)
                }
            }


        }
        Button(enabled = !fpState.requestLoading,colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White
        ), onClick = {
            forgotPasswordViewModel.resetPassword()
        }) {
            if (fpState.requestLoading) {
                Text(text = "Loading...", color = colorResource(id = R.color.darkBlue), fontFamily = outfitFamily)
                Spacer(modifier = Modifier.size(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    color = colorResource(id = R.color.backgroundTop),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Send", color = colorResource(id = R.color.darkBlue), fontFamily = outfitFamily, fontSize = 16.sp)
            }
        }

    }
}
