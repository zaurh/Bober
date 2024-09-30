package com.zaurh.bober.screen.sign_up.sign_up_image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.screen.sign_up.SignUpViewModel
import com.zaurh.bober.screen.sign_up.sign_up_image.components.SUI_Password
import com.zaurh.bober.screen.sign_up.sign_up_image.components.SUI_PickImage

@Composable
fun SignUpImage(
    signUpViewModel: SignUpViewModel
) {
    val focus = LocalFocusManager.current
    val passwordState = signUpViewModel.passwordState.value

    Column(
        modifier = Modifier
            .fillMaxSize()

            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(id = R.color.backgroundTop),
                        colorResource(id = R.color.backgroundBottom)
                    )
                )
            ).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Ready to go", color = Color.White, fontSize = 32.sp)

        Column {
            SUI_PickImage(signUpViewModel)
            Spacer(modifier = Modifier.size(16.dp))
            SUI_Password(signUpViewModel = signUpViewModel)
            Spacer(modifier = Modifier.size(4.dp))
            AnimatedVisibility(visible = passwordState.errorMessage.isNotEmpty()) {
                Row {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.warning_ic),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = passwordState.errorMessage, color = Color.White)
                }
            }
        }


        Column {
            Button(enabled = !signUpViewModel.signUpLoading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                onClick = {
                    focus.clearFocus()
                    signUpViewModel.signUp()
                }) {
                if (signUpViewModel.signUpLoading.value) {
                    Text(text = "Loading...", color = colorResource(id = R.color.darkBlue))
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = colorResource(id = R.color.backgroundTop),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Finish", color = colorResource(id = R.color.darkBlue))
                }
            }
        }
    }

}