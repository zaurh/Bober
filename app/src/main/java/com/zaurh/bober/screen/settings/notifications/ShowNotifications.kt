package com.zaurh.bober.screen.settings.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.ui.MainActivity

/** Intent extra holding the nav route to open when a notification is tapped. Read by [MainActivity]. */
const val EXTRA_NAVIGATE_TO = "navigate_to"

/** Each type has its own channel (created in BoberApp) so users can mute them separately in system settings. */
enum class NotificationType(val channelId: String) {
    CHAT("chat"),
    LIKE("like"),
    MATCH("match"),
}

/**
 * Shows a notification that opens [route] when tapped.
 *
 * @param conversationKey groups notifications: one per chat partner, so a new message replaces the
 * previous one from the same person instead of every notification overwriting each other.
 */
fun showNotification(
    context: Context,
    type: NotificationType,
    title: String,
    text: String,
    route: String,
    conversationKey: String? = null,
) {
    val id = (conversationKey ?: type.name).hashCode()

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(EXTRA_NAVIGATE_TO, route)
    }
    // Distinct request codes, or FLAG_UPDATE_CURRENT would rewrite the route of every pending notification.
    val pendingIntent = PendingIntent.getActivity(
        context,
        id,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, type.channelId)
        .setSmallIcon(context.applicationInfo.icon)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(id, notification)
}

/** Where tapping a chat notification leads: the conversation, or the chat list if the sender is unknown. */
internal fun chatRoute(username: String?): String =
    if (username.isNullOrBlank()) Screen.PagerScreen.route else Screen.ChatScreen.createRoute(username)
