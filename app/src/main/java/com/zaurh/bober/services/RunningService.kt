package com.zaurh.bober.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zaurh.bober.domain.repository.UserRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.screen.settings.notifications.notifyForSocketEvent
import com.zaurh.bober.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class RunningService : Service() {

    companion object {
        /** True while the service runs (app in background); it then owns socket notifications. */
        @Volatile
        var isRunning: Boolean = false
            private set
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var eventsJob: Job? = null

    @Inject
    lateinit var webSocketRepository: WebSocketRepository

    @Inject
    lateinit var userRepository: UserRepository

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        startForegroundService()
        observeSocket()
        return START_NOT_STICKY
    }

    private fun observeSocket() {
        if (eventsJob?.isActive == true) return
        // The connection is shared with the UI and reconnects by itself; this only makes sure it's open.
        webSocketRepository.connect()
        eventsJob = webSocketRepository.events
            .onEach { event ->
                notifyForSocketEvent(
                    context = this@RunningService,
                    event = event,
                    currentUserId = userRepository.userData.value?.id
                )
            }
            .launchIn(serviceScope)
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
            .setContentText("You will still get notifications")
            .setSmallIcon(applicationIcon)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)
    }


    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
        serviceScope.cancel()
    }
}
