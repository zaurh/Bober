package com.zaurh.bober.screen.settings.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {

    fun onNotificationChange(change: Boolean, context: Context, channelId: String) {
        controlNotificationChannel(
            context = context,
            channelId = channelId,
            importanceLevel = if (change) 4 else 0
        )
    }

    private fun controlNotificationChannel(context: Context, channelId: String, importanceLevel: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = notificationManager.getNotificationChannel(channelId) ?: NotificationChannel(channelId, "Channel Name", importanceLevel)
            channel.importance = importanceLevel
        }
    }

}
