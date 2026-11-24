package com.zaurh.bober.screen.auth.sign_in

import android.app.Activity
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.auth.sign_in.components.SignIn_TextField
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun SignInScreen(
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.fillMaxSize().weight(2f), contentAlignment = Alignment.BottomCenter){
            Text(
                text = "Welcome back!", color = Color.White, fontSize = 32.sp,
                fontFamily = outfitFamily,
            )
        }


        Column(
            modifier = Modifier.weight(6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

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
                        Icon(painter = image, contentDescription = "", tint = Color.Gray)
                    }
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    "Forgot password?",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.ForgotPasswordScreen.route)
                    },
                    color = Color.White,
                    textDecoration = TextDecoration.Underline,
                    fontFamily = outfitFamily
                )
            }

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
                    Text(
                        text = "Loading...",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = colorResource(id = R.color.backgroundTop),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign in",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }


        Column(modifier = Modifier.weight(2f),horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                Modifier
                    .clip(RoundedCornerShape(20))
                    .background(Color.White)
                    .clickable {
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
                    Text(
                        text = "Continue with Google",
                        color = colorResource(id = R.color.darkBlue),
                        fontFamily = outfitFamily
                    )
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Don't have an account?", color = Color.White, fontFamily = outfitFamily)
            Text(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.SignUpUsername.route)
                },
                text = "Sign up",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
                fontFamily = outfitFamily
            )
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
            if (googleOneTap.value){
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


@Composable
fun StartActivityForResult(
    key: Any,
    onResultReceived: (String) -> Unit,
    onDialogDismissed: () -> Unit,
    launcher: (ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) -> Unit
) {
    val activity = LocalContext.current as Activity
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->

            Log.d("sdakdsaldsa", "${result.resultCode}")
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val oneTapClient = Identity.getSignInClient(activity)
                    val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val tokenId = credentials.googleIdToken
                    if (tokenId != null){
                        onResultReceived(tokenId)
                    }
                    Log.d("StartActivityForResult", "$tokenId")



                }
                else if (result.resultCode == Activity.RESULT_CANCELED) {
                    Log.d("StartActivityForResult", "User dismissed One Tap dialog")
                    onDialogDismissed()
                }

                else{
                    Log.d("StartActivityForResult", "Error: ${result.resultCode}")
                    onDialogDismissed()
                }
            }catch (e: ApiException){
                when(e.statusCode){
                    CommonStatusCodes.CANCELED -> {
                        Log.d("StartActivityForResult", "Error: CANCELED")
                        onDialogDismissed()
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        Log.d("StartActivityForResult", "Error: NETWORK_ERROR")
                        onDialogDismissed()
                    }
                    else -> {
                        Log.d("StartActivityForResult", "Error: ${e.message}")
                        onDialogDismissed()
                    }
                }
            }

        }
    )
    LaunchedEffect(key1 = key) {
        launcher(activityLauncher)
    }
}


fun signIn(
    activity: Activity,
    launchActivityResult: (IntentSenderRequest) -> Unit
){
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("107296471290-669ebbhg158lsqapptts39j60prtdreb.apps.googleusercontent.com") // Get this from Google Cloud Console
                .setFilterByAuthorizedAccounts(false) // Show to all users
                .build()
        )
        .build()

    oneTapClient
        .beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                Log.d("GoogleSignIn", "$result")

                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            }
            catch (e: Exception){
                Log.d("GoogleSignIn", "Couldn't: ${e.message}")
            }

        }
        .addOnFailureListener { e ->
            Log.e("GoogleSignIn", "One Tap failed: ${e.localizedMessage}")
        }
}