package com.zaurh.bober.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.screen.settings.notifications.preferences.NotificationPreferences
import com.zaurh.bober.screen.settings.notifications.showNotification
import com.zaurh.bober.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RunningService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var webSocketRepository: WebSocketRepository

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startWebSocketConnection()
        return START_NOT_STICKY
    }

    private fun startWebSocketConnection() {
        serviceScope.launch {
            if (serviceScope.isActive) {
                val notification = NotificationPreferences(this@RunningService)

                webSocketRepository.observeMessages().collect { message ->
                    when (message.text) {
                        "typing:",
                        "triggerOnline:",
                        "readMessages:",
                        "deliveredMessage:",
                        "switched to:" -> {}
                        "got_match:" -> {
                            notification.getLikesStatus.collect{
                                if (it){
                                    showNotification(
                                        channelId = "match",
                                        context = this@RunningService,
                                        text = "You've got a new match!",
                                        title = "Bober"
                                    )
                                }
                            }
                        }
                        "like_sent:" -> {
                            notification.getLikesStatus.collect{
                                if (it){
                                    showNotification(
                                        channelId = "like",
                                        context = this@RunningService,
                                        text = "Someone liked you!",
                                        title = "Bober"
                                    )
                                }
                            }
                        }

                        else ->
                            notification.getMessagesStatus.collect{
                                if (it){
                                    val text = if (message.text?.endsWith(".gif") == true) "Sent GIF" else message.text ?: ""

                                    showNotification(
                                        channelId = "like",
                                        context = this@RunningService,
                                        text = text,
                                        title = "${message.senderUsername}"
                                    )
                                }
                            }
                    }
                }
            }
        }
    }


    private fun startForegroundService() {
        val intent = Intent(this, MainActivity::class.java)

        val flag = PendingIntent.FLAG_IMMUTABLE

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            flag
        )
        val applicationIcon = this.applicationInfo.icon

        val notification = NotificationCompat.Builder(this, "foreground")
            .setContentTitle("Bober")
            .setContentText("You will still get message notifications")
            .setSmallIcon(applicationIcon)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)
    }



    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}