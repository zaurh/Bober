package com.zaurh.bober.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.navigation.SetupNavGraph
import com.zaurh.bober.screen.home.HomeViewModel
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.settings.theme.preferences.ThemePreferences
import com.zaurh.bober.screen.sign_in.SignInViewModel
import com.zaurh.bober.ui.theme.BoberTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private lateinit var connectivityObserver: ConnectivityObserver

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE

    private var appReady = false

    @Inject
    lateinit var webSocketRepository: WebSocketRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if(updateType == AppUpdateType.FLEXIBLE){
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
        checkForAppUpdates()



        installSplashScreen().apply {
            setKeepOnScreenCondition { !appReady }
        }

        setContent {

            val navController = rememberNavController()


            val context = LocalContext.current
            val dataStore = ThemePreferences(context)
            var darkTheme by remember { mutableStateOf(false) }
            val savedTheme = dataStore.getDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            val signInViewModel = hiltViewModel<SignInViewModel>()
            val homeScreenViewModel = hiltViewModel<HomeViewModel>()
            val matchViewModel = hiltViewModel<MatchViewModel>()
            val signedIn = signInViewModel.signedInState.collectAsState()
            val loading = signInViewModel.loading.collectAsState()


            BoberTheme(darkTheme = savedTheme.value ?: false) {
                LaunchedEffect(key1 = loading.value) {
                    if (!loading.value) {
                        delay(500)
                        appReady = true
                    }
                }

//                LaunchedEffect(signedIn.value && appReady ) {
//                    chatScreenViewModel.initSession(context)
//                }


                val navigateTo = intent.getStringExtra("navigate_to")


                LaunchedEffect(navigateTo) {
                    if (navigateTo != null) {
                        navController.navigate(navigateTo)
                    }
                }

//                LaunchedEffect(homeScreenViewModel.connectionEnabled.value) {
//                    if(homeScreenViewModel.connectionEnabled.value){
//                        if (!webSocketRepository.isConnected()){
//                            chatScreenViewModel.initSession(context)
//                        }
//                    }
//                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    SetupNavGraph(
                        navController = navController,
                        startDestination =
                        if (signedIn.value) Screen.PagerScreen.route
                        else Screen.SignInScreen.route,
                        darkTheme = savedTheme.value ?: false,
                        onThemeUpdated = {
                            scope.launch {
                                darkTheme = !(savedTheme.value ?: false)
                                dataStore.saveDarkMode(darkTheme)
                            }
                        }
                    )


//                    val gotMatchState = chatScreenViewModel.gotMatch.value
                    AnimatedVisibility(
                        visible = false,
                        enter = fadeIn(animationSpec = tween(2000)),
                        exit = fadeOut(animationSpec = tween(2000))
                    ) {
                        val matchListState = matchViewModel.matchListState.collectAsState()

//                        val recipientUser =
//                            matchListState.value.find { it?.userId == gotMatchState.recipientId }
//                        val recipientImage = recipientUser?.image?.first().orEmpty()
//                        GotMatchAlert(recipientImage = "")
                    }
                }


            }
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED){
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds.",
                Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun checkForAppUpdates(){
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when(updateType){
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed){
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    123
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE){
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        this,
                        123
                    )
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123){
            if (resultCode != RESULT_OK){
                println("Something went wrong.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE){
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }



}




