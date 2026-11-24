package com.zaurh.bober.screen.settings.notifications

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.ui.components.SettingsDivider
import com.zaurh.bober.ui.components.SettingsFootnote
import com.zaurh.bober.ui.components.SettingsGroup
import com.zaurh.bober.ui.components.SettingsRow
import com.zaurh.bober.ui.components.SettingsSwitchRow
import com.zaurh.bober.ui.components.BackTopBar
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences
import kotlinx.coroutines.launch

/** Which kinds of notifications the user gets. System permission comes first; the toggles are ours. */
@Composable
fun NotificationScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { NotificationPreferences(context.applicationContext) }
    // Defaults are "on" (see NotificationPreferences), so start there to avoid toggles flashing off.
    val messages by preferences.getMessagesStatus.collectAsState(initial = true)
    val likes by preferences.getLikesStatus.collectAsState(initial = true)
    val matches by preferences.getMatchStatus.collectAsState(initial = true)

    // The system switch can change while we're in the background (user visits system settings).
    var allowed by remember { mutableStateOf(notificationsAllowed(context)) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { allowed = notificationsAllowed(context) }
    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        allowed = granted || notificationsAllowed(context)
        if (!allowed) openNotificationSettings(context)
    }

    Scaffold(topBar = { BackTopBar(title = "Notifications", onBack = { navController.popBackStack() }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            if (!allowed) {
                SettingsGroup(null, Modifier.padding(top = 12.dp)) {
                    SettingsRow(
                        icon = R.drawable.notification_ic,
                        title = "Notifications are off",
                        subtitle = "Turn them on to hear about new messages and matches.",
                        trailing = {
                            Button(onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    openNotificationSettings(context)
                                }
                            }) { Text("Turn on") }
                        }
                    )
                }
            }

            SettingsGroup("Notify me about") {
                SettingsSwitchRow(
                    icon = R.drawable.chat_ic,
                    title = "Messages",
                    subtitle = "New messages and reactions",
                    checked = messages,
                    enabled = allowed,
                    onCheckedChange = { scope.launch { preferences.controlMessages(it) } }
                )
                SettingsDivider()
                SettingsSwitchRow(
                    icon = R.drawable.like_ic,
                    title = "Likes",
                    subtitle = "Someone liked you",
                    checked = likes,
                    enabled = allowed,
                    onCheckedChange = { scope.launch { preferences.controlLikes(it) } }
                )
                SettingsDivider()
                SettingsSwitchRow(
                    icon = R.drawable.heart_ic,
                    title = "Matches",
                    subtitle = "You've got a new match",
                    checked = matches,
                    enabled = allowed,
                    onCheckedChange = { scope.launch { preferences.controlMatch(it) } }
                )
            }
            SettingsFootnote("Sounds and pop-up style can be changed per type in your phone's notification settings.")
        }
    }
}

/** Covers both the Android 13+ runtime permission and the app-wide system switch on older versions. */
private fun notificationsAllowed(context: Context): Boolean =
    NotificationManagerCompat.from(context).areNotificationsEnabled()

fun openNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    } else {
        // No per-app notification page before Android 8: open the app's system page instead.
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.fromParts("package", context.packageName, null))
    }
    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}
