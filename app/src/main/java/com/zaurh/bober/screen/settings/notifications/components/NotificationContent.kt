package com.zaurh.bober.screen.settings.notifications.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.zaurh.bober.screen.settings.notifications.NotificationViewModel
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationContent(
    paddingValues: PaddingValues,
    notificationViewModel: NotificationViewModel
) {
    val context = LocalContext.current
    val dataStore = NotificationPreferences(context)
    val scope = rememberCoroutineScope()

    val messagesStatus = dataStore.getMessagesStatus.collectAsState(initial = false)
    val likesStatus = dataStore.getLikesStatus.collectAsState(initial = false)
    val matchStatus = dataStore.getMatchStatus.collectAsState(initial = false)


    val notificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (notificationPermission.status.isGranted) {

                NotificationItem(
                    checked = messagesStatus.value,
                    title = "Messages",
                    description = "Someone send you a new message.",
                    onChange = {
                        scope.launch {
                            dataStore.controlMessages(it)
                        }
                        notificationViewModel.onNotificationChange(
                            change = it,
                            context = context,
                            channelId = "chat"
                        )
                    }
                )
                Spacer(modifier = Modifier.size(12.dp))
                NotificationItem(
                    checked = likesStatus.value,
                    title = "Likes",
                    description = "Someone liked you!",
                    onChange = {
                        scope.launch {
                            dataStore.controlLikes(it)
                        }
                        notificationViewModel.onNotificationChange(
                            change = it,
                            context = context,
                            channelId = "likes"
                        )
                    }
                )
                Spacer(modifier = Modifier.size(12.dp))
                NotificationItem(
                    checked = matchStatus.value,
                    title = "Match",
                    description = "You've got a new match!",
                    onChange = {
                        scope.launch {
                            dataStore.controlMatch(it)
                        }
                        notificationViewModel.onNotificationChange(
                            change = it,
                            context = context,
                            channelId = "match"
                        )
                    }
                )
            } else {
                NotificationItem(
                    checked = false,
                    title = "Push notifications",
                    description = "You need to enable notifications manually",
                    onChange = {
                        openNotificationSettings(
                            context
                        )

                    }
                )
            }
        }


    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}