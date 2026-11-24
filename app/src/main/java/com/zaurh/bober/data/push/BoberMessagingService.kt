package com.zaurh.bober.data.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zaurh.bober.data.remote.TokenStore
import com.zaurh.bober.screen.settings.notifications.PushKind
import com.zaurh.bober.screen.settings.notifications.showPushNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Receives the backend's data pushes (new message, reaction, like, match) while the app is in the
 * background or closed, and shows them according to the user's notification settings.
 */
@AndroidEntryPoint
class BoberMessagingService : FirebaseMessagingService() {

    @Inject lateinit var tokenStore: TokenStore
    @Inject lateinit var registrar: PushTokenRegistrar

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        // Only signed-in users have somewhere to store it; sign-in registers it otherwise.
        if (tokenStore.hasToken) scope.launch { registrar.upload(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (!tokenStore.hasToken) return // signed out since the push was sent
        val data = message.data
        val kind = PushKind.from(data["type"]) ?: return
        // onMessageReceived runs on a background thread and must finish before returning.
        runBlocking {
            showPushNotification(
                context = applicationContext,
                kind = kind,
                title = data["title"].orEmpty().ifEmpty { "Bober" },
                text = data["body"].orEmpty(),
                senderId = data["senderId"],
                senderUsername = data["senderUsername"],
            )
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
