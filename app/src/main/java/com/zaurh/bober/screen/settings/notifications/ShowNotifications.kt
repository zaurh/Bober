package com.zaurh.bober.screen.settings.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.zaurh.bober.ui.MainActivity

fun showNotification(
    channelId: String,
    context: Context,
    title: String,
    text: String
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }


    val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        flag
    )
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val applicationIcon = context.applicationInfo.icon


    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(applicationIcon)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(1, notificationBuilder.build())
}
