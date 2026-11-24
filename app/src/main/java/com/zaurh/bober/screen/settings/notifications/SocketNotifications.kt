package com.zaurh.bober.screen.settings.notifications

import android.content.Context
import com.zaurh.bober.data.websocket.SocketEvent
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Shows the local notification for a chat socket event, respecting the user's notification settings.
 *
 * @param currentUserId used to skip the echo of the user's own messages.
 * @param openChatWith the user whose chat is on screen, if any; their messages don't need a notification.
 */
suspend fun notifyForSocketEvent(
    context: Context,
    event: SocketEvent,
    currentUserId: String?,
    openChatWith: String? = null,
) {
    val preferences = NotificationPreferences(context)
    when (event) {
        is SocketEvent.NewMessage -> {
            val message = event.message
            if (message.senderUserId == currentUserId || message.senderUserId == openChatWith) return
            if (!preferences.getMessagesStatus.isEnabled()) return
            showNotification(
                context = context,
                type = NotificationType.CHAT,
                title = message.senderUsername,
                text = if (message.text.endsWith(".gif")) "Sent GIF" else message.text,
                route = chatRoute(message.senderUsername),
                conversationKey = message.senderUserId,
            )
        }

        is SocketEvent.LikeReceived -> {
            if (!preferences.getLikesStatus.isEnabled()) return
            showNotification(
                context = context,
                type = NotificationType.LIKE,
                title = "Bober",
                text = "Someone liked you!",
                route = Screen.WhoLikesScreen.route,
            )
        }

        is SocketEvent.Matched -> {
            // The user who made the match sees the in-app match animation instead.
            if (event.match.senderUserId == currentUserId) return
            if (!preferences.getMatchStatus.isEnabled()) return
            showNotification(
                context = context,
                type = NotificationType.MATCH,
                title = "Bober",
                text = "You've got a new match!",
                route = Screen.PagerScreen.route,
            )
        }

        else -> Unit
    }
}

/** Reads the current value once. (Collecting would never return and re-fire on every settings change.) */
internal suspend fun Flow<Boolean>.isEnabled(): Boolean = first()
