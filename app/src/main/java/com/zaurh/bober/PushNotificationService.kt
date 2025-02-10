package com.zaurh.bober

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zaurh.bober.ui.MainActivity

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if there's any data payload
        if (remoteMessage.data.isNotEmpty()) {
            val deepLink = remoteMessage.data["deepLink"]
            val screen = remoteMessage.data["screen"]  // Ensure you get the screen data correctly
            Log.d("firebase_message", "screen: $screen")

            // Now, send the notification with the screen info
            sendNotification(screen)
        }
    }

    private fun sendNotification(screen: String?) {
        // Create the Intent to open MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            // Pass the 'screen' data as an extra in the Intent
            putExtra("screen", screen)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create a PendingIntent
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.drawable.blocked_users_ic)
            .setContentTitle("New Notification")
            .setContentText("Tap to go to $screen")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}
