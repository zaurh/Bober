package com.zaurh.bober.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.settings.theme.preferences.ThemePreferences
import com.zaurh.bober.screen.sign_in.SignInViewModel
import com.zaurh.bober.services.RunningService
import com.zaurh.bober.ui.theme.BoberTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var appReady = false


    @Inject
    lateinit var webSocketRepository: WebSocketRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen().apply {
            setKeepOnScreenCondition { !appReady }
        }


        setContent {

            val context = LocalContext.current
            val dataStore = ThemePreferences(context)
            var darkTheme by remember { mutableStateOf(false) }
            val savedTheme = dataStore.getDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val signInViewModel = hiltViewModel<SignInViewModel>()
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            val signedIn = signInViewModel.signedInState.collectAsState()
            val loading = signInViewModel.loading.collectAsState()

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
                profileViewModel.clearProfileData()
            }

            BoberTheme(darkTheme = savedTheme.value ?: false) {
                LaunchedEffect(key1 = loading.value) {
                    if (!loading.value){
                        delay(500)
                        appReady = true
                    }
                }
                LaunchedEffect(key1 = signedIn.value) {
                    if (!signedIn.value) {
                        webSocketRepository.closeSession()
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
                }

            }
        }
    }

}




