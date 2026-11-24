package com.zaurh.bober

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.zaurh.bober.screen.settings.notifications.NotificationType
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BoberApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        // Used by the old always-on background service, which push notifications replaced.
        manager.deleteNotificationChannel("foreground")
        manager.createNotificationChannels(
            NotificationType.entries.map { type ->
                val (name, description) = when (type) {
                    NotificationType.CHAT -> "Messages" to "New messages and reactions"
                    NotificationType.LIKE -> "Likes" to "When someone likes you"
                    NotificationType.MATCH -> "Matches" to "When you get a new match"
                }
                NotificationChannel(type.channelId, name, NotificationManager.IMPORTANCE_HIGH).apply {
                    this.description = description
                }
            }
        )
    }
}
