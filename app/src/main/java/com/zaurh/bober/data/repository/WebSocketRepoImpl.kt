package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.remote.TokenStore
import com.zaurh.bober.data.websocket.SocketEvent
import com.zaurh.bober.data.websocket.SocketEventParser
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.util.Constants.WEBSOCKET_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class WebSocketRepoImpl @Inject constructor(
    private val client: OkHttpClient,
    private val tokenStore: TokenStore,
) : WebSocketRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val parser = SocketEventParser()
    private val lock = Any()

    private val _events = MutableSharedFlow<SocketEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events: SharedFlow<SocketEvent> = _events

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected

    // Written only while holding [lock].
    @Volatile
    private var socket: WebSocket? = null
    private var shouldBeConnected = false
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0

    /** The backend remembers the recipient per connection, so it is re-sent after every reconnect. */
    @Volatile
    private var currentRecipientId: String? = null

    override fun connect() {
        synchronized(lock) {
            shouldBeConnected = true
            if (socket == null && reconnectJob?.isActive != true) openSocket()
        }
    }

    override fun disconnect() {
        synchronized(lock) {
            shouldBeConnected = false
            reconnectJob?.cancel()
            reconnectJob = null
            currentRecipientId = null
            socket?.close(NORMAL_CLOSURE, null)
            socket = null
            _isConnected.value = false
        }
    }

    override fun switchRecipient(recipientUserId: String) {
        currentRecipientId = recipientUserId
        send("switchRecipient:$recipientUserId")
    }

    override fun sendTyping() {
        send("typing:")
    }

    override fun markMessagesRead() {
        send("readMessages:")
    }

    override fun sendMessage(text: String): Boolean = send(text)

    private fun send(text: String): Boolean {
        val open = synchronized(lock) { socket.takeIf { _isConnected.value } }
        return open?.send(text) ?: false
    }

    /** Must be called with [lock] held. */
    private fun openSocket() {
        if (!tokenStore.hasToken) {
            Log.w(TAG, "Not connecting: no session token")
            return
        }
        // The Authorization header is added by AuthInterceptor.
        val request = Request.Builder().url(WEBSOCKET_URL).build()
        socket = client.newWebSocket(request, Listener())
    }

    private inner class Listener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            synchronized(lock) {
                if (webSocket !== socket) return
                reconnectAttempt = 0
                _isConnected.value = true
            }
            currentRecipientId?.let { webSocket.send("switchRecipient:$it") }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (webSocket !== socket) return
            _events.tryEmit(parser.parse(text))
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            // The server closes with VIOLATED_POLICY when the account no longer exists.
            onConnectionLost(webSocket, retry = code != POLICY_VIOLATION)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.w(TAG, "Socket failure: ${t.message}")
            // 401: the token was rejected; reconnecting with the same token can't succeed.
            onConnectionLost(webSocket, retry = response?.code != HttpURLConnection.HTTP_UNAUTHORIZED)
        }
    }

    private fun onConnectionLost(webSocket: WebSocket, retry: Boolean) {
        synchronized(lock) {
            if (webSocket !== socket) return // An old socket we already replaced.
            socket = null
            _isConnected.value = false
            if (retry && shouldBeConnected) scheduleReconnect()
        }
    }

    /** Must be called with [lock] held. */
    private fun scheduleReconnect() {
        val delayMillis = min(MAX_BACKOFF_MS, INITIAL_BACKOFF_MS shl min(reconnectAttempt, 5))
        reconnectAttempt++
        reconnectJob = scope.launch {
            delay(delayMillis)
            synchronized(lock) {
                reconnectJob = null
                if (shouldBeConnected && socket == null) openSocket()
            }
        }
    }

    private companion object {
        const val TAG = "WebSocketRepo"
        const val NORMAL_CLOSURE = 1000
        const val POLICY_VIOLATION = 1008
        const val INITIAL_BACKOFF_MS = 1_000L
        const val MAX_BACKOFF_MS = 30_000L
    }
}
