package com.zaurh.bober.screen.settings.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.settings.notifications.components.NotificationContent
import com.zaurh.bober.screen.settings.notifications.components.NotificationTopBar

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {


    Scaffold(
        topBar = {
            NotificationTopBar(navController = navController)
        },
        content = { paddingValues ->
            NotificationContent(
                paddingValues = paddingValues,
                notificationViewModel = notificationViewModel
            )
        }
    )


}