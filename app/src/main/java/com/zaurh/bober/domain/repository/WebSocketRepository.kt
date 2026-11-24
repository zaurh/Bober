package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.websocket.SocketEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The app-wide connection to the backend's `/chat-socket`.
 *
 * There is a single connection; every collector of [events] receives every event. Once [connect] is
 * called the connection is kept alive and re-established after network drops until [disconnect].
 */
interface WebSocketRepository {

    val events: SharedFlow<SocketEvent>

    /** True while the socket is open. Use it to resync (e.g. refetch messages) after a reconnect. */
    val isConnected: StateFlow<Boolean>

    /** Opens the connection if it isn't open yet. Safe to call repeatedly. */
    fun connect()

    /** Closes the connection and stops reconnecting. */
    fun disconnect()

    /** Selects the chat partner that subsequent messages, typing and read receipts are for. */
    fun switchRecipient(recipientUserId: String)

    fun sendTyping()

    /** Marks every message from the current recipient as read. */
    fun markMessagesRead()

    /** Sends a chat message to the current recipient. Returns false if the socket isn't open. */
    fun sendMessage(text: String): Boolean
}
