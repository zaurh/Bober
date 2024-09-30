package com.zaurh.bober.screen.sign_in

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.sign_in.components.SignIn_TextField

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel
) {
    val context = LocalContext.current
    val signedIn = signInViewModel.signedInState.collectAsState()

    if (signedIn.value) {
        navController.navigate(Screen.PagerScreen.route)
    }

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
            )
            .padding(start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "BOBER", color = Color.White, fontSize = 32.sp)
            Spacer(modifier = Modifier.size(16.dp))
            Icon(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.bober_ic),
                contentDescription = "",
                tint = Color.White
            )
            Spacer(Modifier.size(16.dp))

            SignIn_TextField(
                label = "Username",
                value = signInViewModel.usernameState.value,
                onValueChange = signInViewModel::onUsernameChange,
                keyboardType = KeyboardType.Text,
                visualTransformation = VisualTransformation.None
            )
            Spacer(modifier = Modifier.size(8.dp))
            SignIn_TextField(
                label = "Password",
                value = signInViewModel.passwordState.value,
                onValueChange = signInViewModel::onPasswordChange,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (signInViewModel.passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (signInViewModel.passwordVisible.value)
                        painterResource(id = R.drawable.visibilityon_ic)
                    else painterResource(id = R.drawable.visibilityoff_ic)

                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = signInViewModel::onPasswordVisibilityChange
                    ) {
                        Icon(painter = image, contentDescription = "")
                    }
                }
            )
            Spacer(modifier = Modifier.size(16.dp))

            Button(enabled = !signInViewModel.signInLoading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                onClick = {
                    signInViewModel.signIn(context)
                }) {
                if (signInViewModel.signInLoading.value || signedIn.value) {
                    Text(text = "Loading...", color = colorResource(id = R.color.darkBlue))
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = colorResource(id = R.color.backgroundTop),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Sign in", color = colorResource(id = R.color.darkBlue))
                }
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .width(80.dp)
                    .alpha(0.5f), color = Color.White
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = "OR",
                color = Color.White
            )
            HorizontalDivider(
                modifier = Modifier
                    .width(80.dp)
                    .alpha(0.5f), color = Color.White
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(20))
                .background(Color.White)
                .clickable {
                    Toast
                        .makeText(
                            context,
                            "Not available yet",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
        ) {
            Row(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Continue with Google",
                    color = colorResource(id = R.color.darkBlue)
                )
            }

        }
        Spacer(Modifier.size(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Don't have an account?", color = Color.White)
            Text(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.SignUpUsername.route)
                },
                text = "Sign up",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        }

    }

}
