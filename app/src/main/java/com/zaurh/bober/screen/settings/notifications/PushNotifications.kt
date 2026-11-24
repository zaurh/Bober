package com.zaurh.bober.screen.settings.notifications

import android.content.Context
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences

/** The `type` field of a backend data push (BoberBackend's `PushType`). */
enum class PushKind(val wireName: String) {
    MESSAGE("message"),
    REACTION("reaction"),
    LIKE("like"),
    MATCH("match");

    companion object {
        fun from(value: String?): PushKind? = entries.firstOrNull { it.wireName == value }
    }
}

/**
 * Shows the notification for an FCM push, respecting the user's notification settings.
 * Reactions follow the "messages" setting since they happen inside a chat.
 */
suspend fun showPushNotification(
    context: Context,
    kind: PushKind,
    title: String,
    text: String,
    senderId: String?,
    senderUsername: String?,
) {
    val preferences = NotificationPreferences(context)
    when (kind) {
        PushKind.MESSAGE, PushKind.REACTION -> {
            if (!preferences.getMessagesStatus.isEnabled()) return
            showNotification(
                context = context,
                type = NotificationType.CHAT,
                title = title,
                text = text,
                route = chatRoute(senderUsername),
                conversationKey = senderId,
            )
        }

        PushKind.LIKE -> {
            if (!preferences.getLikesStatus.isEnabled()) return
            showNotification(
                context = context,
                type = NotificationType.LIKE,
                title = title,
                text = text,
                route = Screen.WhoLikesScreen.route,
            )
        }

        PushKind.MATCH -> {
            if (!preferences.getMatchStatus.isEnabled()) return
            showNotification(
                context = context,
                type = NotificationType.MATCH,
                title = title,
                text = text,
                // A match push names the other user, so go straight to the new conversation.
                route = chatRoute(senderUsername),
            )
        }
    }
}
