package com.zaurh.bober.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import com.zaurh.bober.screen.password.ResetPasswordDialog
import com.zaurh.bober.screen.password.PasswordViewModel
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.navigation.SetupNavGraph
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.match.components.GotMatchAlert
import com.zaurh.bober.screen.settings.email.EmailLinkResultDialog
import com.zaurh.bober.screen.settings.email.EmailVerificationViewModel
import com.zaurh.bober.screen.settings.notifications.EXTRA_NAVIGATE_TO
import com.zaurh.bober.screen.settings.theme.preferences.ThemePreferences
import com.zaurh.bober.screen.sign_in.SignInViewModel
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

    /** Token from an opened `…/reset-password?token=…` link. Works signed out. */
    private var pendingResetToken by mutableStateOf<String?>(null)

    /** Route from a tapped notification, waiting until the session check has finished. */
    private var pendingRoute by mutableStateOf<String?>(null)


    @Inject
    lateinit var webSocketRepository: WebSocketRepository

    private lateinit var inAppUpdates: InAppUpdates

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen().apply {
            setKeepOnScreenCondition { !appReady }
        }
        // Draw behind the system bars; screens handle insets (top app bars, nav bar, auth gradient).
        enableEdgeToEdge()

        inAppUpdates = InAppUpdates(this)
        if (savedInstanceState == null) inAppUpdates.check()

        // On recreation (e.g. rotation) the link was already handled.
        if (savedInstanceState == null) {
            consumeEmailVerificationLink(intent)
            consumeNotificationRoute(intent)
        }


        setContent {

            val context = LocalContext.current
            val dataStore = ThemePreferences(context)
            // The user's choice, or the system theme until they make one.
            val savedTheme = dataStore.getDarkMode.collectAsState(initial = null)
            val darkTheme = savedTheme.value ?: isSystemInDarkTheme()
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val signInViewModel = hiltViewModel<SignInViewModel>()
            val chatScreenViewModel = hiltViewModel<ChatScreenViewModel>()
            val signedIn = signInViewModel.signedInState.collectAsState()
            val loading = signInViewModel.loading.collectAsState()
            val emailVerificationViewModel = hiltViewModel<EmailVerificationViewModel>()
            val passwordViewModel = hiltViewModel<PasswordViewModel>()



            BoberTheme(darkTheme = darkTheme) {


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

                val resetToken = pendingResetToken
                LaunchedEffect(resetToken) {
                    if (resetToken != null) {
                        pendingResetToken = null
                        passwordViewModel.openReset(resetToken)
                    }
                }

                // Opened from a notification: go to its screen once we know the user is signed in.
                val route = pendingRoute
                LaunchedEffect(route, loading.value, signedIn.value) {
                    if (route == null || loading.value) return@LaunchedEffect
                    pendingRoute = null
                    if (signedIn.value) navController.navigate(route) { launchSingleTop = true }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    SetupNavGraph(
                        navController = navController,
                        startDestination = if (signedIn.value) Screen.PagerScreen.route else Screen.SignInScreen.route,
                        darkTheme = darkTheme,
                        onThemeUpdated = {
                            scope.launch {
                                dataStore.saveDarkMode(!darkTheme)
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
                    ResetPasswordDialog(viewModel = passwordViewModel)
                    if (inAppUpdates.updateReady) {
                        AlertDialog(
                            onDismissRequest = inAppUpdates::later,
                            title = { Text("Update ready") },
                            text = { Text("A new version of Bober has been downloaded. Restart to finish updating.") },
                            confirmButton = { TextButton(onClick = inAppUpdates::install) { Text("Restart") } },
                            dismissButton = { TextButton(onClick = inAppUpdates::later) { Text("Later") } }
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

    override fun onStart() {
        super.onStart()
        // Foreground: live chat over the socket (no-op when signed out).
        webSocketRepository.connect()
    }

    override fun onStop() {
        super.onStop()
        // Background: go offline so the backend sends push notifications instead.
        if (!isChangingConfigurations) webSocketRepository.pause()
    }

    override fun onResume() {
        super.onResume()
        inAppUpdates.onResume()
    }

    override fun onDestroy() {
        inAppUpdates.release()
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        consumeEmailVerificationLink(intent)
        consumeNotificationRoute(intent)
    }

    private fun consumeNotificationRoute(intent: Intent?) {
        val route = intent?.getStringExtra(EXTRA_NAVIGATE_TO) ?: return
        pendingRoute = route
        intent.removeExtra(EXTRA_NAVIGATE_TO)
    }

    /** Handles the backend's email links: `/verify_email?token=…` and `/reset-password?token=…`. */
    private fun consumeEmailVerificationLink(intent: Intent?) {
        val data = intent?.data ?: return
        when (linkPath(data)) {
            "/verify_email" -> pendingEmailToken = linkToken(data) ?: return
            "/reset-password" -> pendingResetToken = linkToken(data) ?: return
            else -> return
        }
        intent.data = null
    }

    private fun linkPath(uri: Uri): String? {
        val backend = Uri.parse(Constants.BASE_URL)
        return uri.path.takeIf { uri.scheme == "https" && uri.host == backend.host }
    }

    private fun linkToken(uri: Uri): String? = uri.getQueryParameter("token")?.takeIf { it.isNotBlank() }
}
