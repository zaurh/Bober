package com.zaurh.bober.screen.sign_up.sign_up_username

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_up.SignUpViewModel

@Composable
fun SignUpUsername(
    signUpViewModel: SignUpViewModel,
    navController: NavController
) {
    val usernameState = signUpViewModel.usernameState.value

    val focus = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(id = R.color.backgroundTop),
                        colorResource(id = R.color.backgroundBottom)
                    )
                )
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Hi there!", color = Color.White, fontSize = 32.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Box {
                Icon(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.bober_ic),
                    contentDescription = "",
                    tint = Color.White
                )
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .rotate(-10f), onClick = {
                    }, border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text(text = "@bober", color = colorResource(id = R.color.darkBlue))
                }
            }

            Spacer(modifier = Modifier.size(32.dp))
            Text(text = "Pick yourself a username", color = Color.White)
            Spacer(modifier = Modifier.size(16.dp))
            TextField(
                modifier = Modifier.clip(RoundedCornerShape(40)),
                leadingIcon = { Text(text = "@", fontWeight = FontWeight.Bold) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.darkBlue),
                    unfocusedTextColor = colorResource(id = R.color.darkBlue),
                    focusedTextColor = colorResource(id = R.color.darkBlue)
                ),
                value = signUpViewModel.usernameState.value.username,
                onValueChange = signUpViewModel::onUsernameChange,
                isError = usernameState.errorMessage.isNotEmpty(),
                placeholder = { Text(text = "Username", color = Color.LightGray) },
                singleLine = true
            )
            Spacer(modifier = Modifier.size(4.dp))
            AnimatedVisibility(visible = usernameState.errorMessage.isNotEmpty()) {
                Row {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.warning_ic),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = usernameState.errorMessage, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ), onClick = {
                signUpViewModel.checkUsername {
                    focus.clearFocus()
                    navController.navigate(Screen.SignUpGender.route)
                }
            }) {
                if (usernameState.loading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Checking...", color = colorResource(id = R.color.darkBlue))
                        Spacer(modifier = Modifier.size(4.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = colorResource(id = R.color.backgroundBottom),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Text(text = "Next", color = colorResource(id = R.color.darkBlue))
                }
            }
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Already have an account?", color = Color.White)
            Text(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.SignInScreen.route) {
                        popUpTo(0)
                    }
                },
                text = "Sign in",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )

        }
    }
}