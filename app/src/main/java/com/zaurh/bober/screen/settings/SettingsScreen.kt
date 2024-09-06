package com.zaurh.bober.screen.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.settings.components.SettingsContent
import com.zaurh.bober.screen.settings.components.SettingsTopBar
import com.zaurh.bober.screen.settings.components.modal_sheets.SettingsShowMeMS

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    navController: NavController,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            SettingsTopBar(navController = navController)
        },
        content = { paddingValues ->
            SettingsContent(
                paddingValues = paddingValues,
                navController = navController,
                settingsViewModel = settingsViewModel,
                darkTheme = darkTheme,
                onThemeUpdated = onThemeUpdated
            )
        }
    )

    SettingsShowMeMS(settingsViewModel = settingsViewModel)



}
