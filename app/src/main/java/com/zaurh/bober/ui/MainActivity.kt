package com.zaurh.bober.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.rememberNavController
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.navigation.SetupNavGraph
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.match.components.GotMatchAlert
import com.zaurh.bober.screen.settings.email.EmailLinkResultDialog
import com.zaurh.bober.screen.settings.email.EmailVerificationViewModel
import com.zaurh.bober.screen.settings.theme.preferences.ThemePreferences
import com.zaurh.bober.screen.sign_in.SignInViewModel
import com.zaurh.bober.services.RunningService
import com.zaurh.bober.ui.theme.BoberTheme
import com.zaurh.bober.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var appReady = false

    /** Token from an opened `…/verify_email?token=…` link, waiting until the user is signed in. */
    private var pendingEmailToken by mutableStateOf<String?>(null)


    @Inject
    lateinit var webSocketRepository: WebSocketRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen().apply {
            setKeepOnScreenCondition { !appReady }
        }
        // Draw behind the system bars; screens handle insets (top app bars, nav bar, auth gradient).
        enableEdgeToEdge()

        // On recreation (e.g. rotation) the link was already handled.
        if (savedInstanceState == null) consumeEmailVerificationLink(intent)


        setContent {

            val context = LocalContext.current
            val dataStore = ThemePreferences(context)
            var darkTheme by remember { mutableStateOf(false) }
            val savedTheme = dataStore.getDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val signInViewModel = hiltViewModel<SignInViewModel>()
            val chatScreenViewModel = hiltViewModel<ChatScreenViewModel>()
            val signedIn = signInViewModel.signedInState.collectAsState()
            val loading = signInViewModel.loading.collectAsState()
            val emailVerificationViewModel = hiltViewModel<EmailVerificationViewModel>()

            fun startForegroundService(context: Context) {
                val intent = Intent(context, RunningService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }

            LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
                startForegroundService(context)
//                profileViewModel.clearProfileData()
            }

            BoberTheme(darkTheme = savedTheme.value ?: false) {


                LaunchedEffect(key1 = loading.value) {
                    if (!loading.value){
                        delay(500)
                        appReady = true
                    }
                }
                val authRoutes = setOf(
                    Screen.SignInScreen.route, Screen.SignUpUsername.route, Screen.SignUpGender.route,
                    Screen.SignUpAbout.route, Screen.SignUpImage.route
                )
                LaunchedEffect(signedIn.value, loading.value) {
                    if (!signedIn.value && !loading.value) {
                        webSocketRepository.disconnect()
                        // Session ended (logout elsewhere, or the server rejected the token): back to sign-in.
                        val current = navController.currentDestination?.route
                        if (current != null && current !in authRoutes) {
                            navController.navigate(Screen.SignInScreen.route) { popUpTo(0) }
                        }
                    }
                }

                // The backend's /validate_email_token needs the JWT, so wait for sign-in before verifying.
                val emailToken = pendingEmailToken
                LaunchedEffect(emailToken, loading.value, signedIn.value) {
                    if (emailToken == null || loading.value) return@LaunchedEffect
                    if (signedIn.value) {
                        pendingEmailToken = null
                        emailVerificationViewModel.verifyLinkToken(emailToken)
                    } else {
                        Toast.makeText(context, "Sign in to finish verifying your email.", Toast.LENGTH_LONG).show()
                    }
                }

                val navigateTo = intent.getStringExtra("navigate_to")
                LaunchedEffect(navigateTo) {
                    if (navigateTo != null) {
                        navController.navigate(navigateTo)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    SetupNavGraph(
                        navController = navController,
                        startDestination = if (signedIn.value) Screen.PagerScreen.route else Screen.SignInScreen.route,
                        darkTheme = savedTheme.value ?: false,
                        onThemeUpdated = {
                            scope.launch {
                                darkTheme = !(savedTheme.value ?: false)
                                dataStore.saveDarkMode(darkTheme)
                            }
                        }
                    )
                    val gotMatchState = chatScreenViewModel.gotMatch.value
                    AnimatedVisibility(
                        visible = gotMatchState.gotMatch,
                        enter = fadeIn(animationSpec = tween(400)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        GotMatchAlert(
                            recipientImage = gotMatchState.recipientImage,
                            recipientName = gotMatchState.recipientUsername,
                            onSendMessage = {
                                chatScreenViewModel.dismissMatch()
                                if (gotMatchState.recipientUsername.isNotEmpty()) {
                                    navController.navigate(Screen.ChatScreen.createRoute(gotMatchState.recipientUsername))
                                }
                            },
                            onDismiss = chatScreenViewModel::dismissMatch
                        )
                    }
                    EmailLinkResultDialog(
                        state = emailVerificationViewModel.link.value,
                        onDismiss = emailVerificationViewModel::dismissLinkResult
                    )
                }


            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        consumeEmailVerificationLink(intent)
    }

    private fun consumeEmailVerificationLink(intent: Intent?) {
        val data = intent?.data ?: return
        val token = emailVerificationToken(data) ?: return
        pendingEmailToken = token
        intent.data = null
    }

    private fun emailVerificationToken(uri: Uri): String? {
        val backend = Uri.parse(Constants.BASE_URL)
        if (uri.scheme != "https" || uri.host != backend.host || uri.path != "/verify_email") return null
        return uri.getQueryParameter("token")?.takeIf { it.isNotBlank() }
    }
}
