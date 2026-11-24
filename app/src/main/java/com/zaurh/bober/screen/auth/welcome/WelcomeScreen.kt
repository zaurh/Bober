package com.zaurh.bober.screen.auth.welcome

import android.app.Activity
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.auth.sign_in.SignInViewModel
import com.zaurh.bober.screen.auth.sign_in.StartActivityForResult
import com.zaurh.bober.screen.auth.sign_in.signIn
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun WelcomeScreen(
    navController: NavController,
    signInViewModel: SignInViewModel,
) {
    val context = LocalContext.current
    val signedIn = signInViewModel.signedInState.collectAsState()
    val activity = context as Activity
    val googleOneTap = signInViewModel.googleOneTap.collectAsState()


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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BOBER", color = Color.White, fontSize = 32.sp,
                fontFamily = FontFamily(
                    Font(R.font.bober_font)
                ),
            )
            Spacer(modifier = Modifier.size(16.dp))
            Icon(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.bober_ic),
                contentDescription = "",
                tint = Color.White
            )
            Spacer(Modifier.size(16.dp))

            Button(modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.White
                ),
                onClick = {
                    navController.navigate(Screen.SignUpUsername.route)
                }) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = "Create an account",
                    color = colorResource(id = R.color.darkBlue),
                    fontFamily = outfitFamily,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.size(4.dp))
            Button(modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.2f),
                    disabledContainerColor = Color.White
                ),
                onClick = {
                    navController.navigate(Screen.SignInScreen.route)
                }) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = "Login",
                    color = Color.White,
                    fontFamily = outfitFamily,
                    fontSize = 16.sp
                )
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
                .clickable(enabled = !signInViewModel.signInLoading.value || !signedIn.value) {
                    signInViewModel.triggerGoogleOneTap(true)
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
                if (signInViewModel.signInLoading.value || signedIn.value) {
                    Text(
                        text = "Signing in...",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = colorResource(id = R.color.backgroundTop),
                        strokeWidth = 2.dp
                    )
                }else{
                    Text(
                        text = "Continue with Google",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily
                    )
                }

            }

        }


    }

    StartActivityForResult(
        key = googleOneTap.value,
        onResultReceived = { tokenId ->
            signInViewModel.signInGoogle(context, tokenId)
        },
        onDialogDismissed = {
            signInViewModel.triggerGoogleOneTap(false)
        },
        launcher = { activityLauncher ->
            if (googleOneTap.value) {
                signIn(
                    activity = activity,
                    launchActivityResult = { intentSenderRequest ->
                        activityLauncher.launch(intentSenderRequest)
                    }
                )
            }
        }
    )

}
