package com.zaurh.bober.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.zaurh.bober.data.message.MessageDataDto
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.util.Constants.WEBSOCKET_URL
import com.zaurh.bober.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WebSocketRepoImpl @Inject constructor(
    private val client: HttpClient,
    private val prefs: SharedPreferences,
) : WebSocketRepository {

    private var socket: WebSocketSession? = null


    override suspend fun initSession(): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                if (socket == null || socket?.isActive == false) {
                    val token = prefs.getString("jwt", null) ?: ""
                    url(WEBSOCKET_URL)
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Couldn't establish a connection.")
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't establish a connection. ${e.message}")
        }
    }

    override suspend fun switchRecipient(
        newRecipientUserId: String
    ) {
        try {
            socket?.let {
                if (it.isActive) {
                    it.send("switchRecipient:$newRecipientUserId")
                }
            }
        } catch (e: Exception) {
            Log.d("Error", "Switch recipient: ${e.message}")
        }
    }

    override suspend fun typingMessage(): Resource<Unit> {
        return try {
            socket?.let {
                if (it.isActive) {
                    it.send("typing:")
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "")
        }
    }

    override suspend fun messageIsDelivered(messageId: String): Resource<Unit> {
        return try {
            socket?.let {
                if (it.isActive) {
                    it.send("deliveredMessage:$messageId")
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "")
        }
    }

    override suspend fun messageIsRead(messageId: String): Resource<Unit> {
        return try {
            socket?.let {
                if (it.isActive) {
                    it.send("readMessages:$messageId")
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "")
        }
    }

    override suspend fun triggerOnline(): Resource<Unit> {
        return try {
            socket?.let {
                if (it.isActive) {
                    it.send("triggerOnline:")
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "")
        }
    }


    override suspend fun sendMessage(
        message: String
    ): Resource<Unit> {
        return try {
            if (socket?.isActive == true) {
                socket?.send(Frame.Text(message))
                Resource.Success(Unit)
            } else {
                Resource.Error("Socket is not active")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun sendLike(profileId: String): Resource<Unit> {
        return try {
            if (socket?.isActive == true) {
                socket?.send(Frame.Text("like_sent:$profileId"))
                Resource.Success(Unit)
            } else {
                Resource.Error("Socket is not active")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }


    override fun observeMessages(): Flow<MessageData> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""

                    if (json.startsWith("Switched recipient to")) {
                        MessageData(text = "switched to:")
                    } else if (json.startsWith("typing:")) {
                        val senderUserId = json.removePrefix("typing:")
                        MessageData(text = "typing:", senderUserId = senderUserId)
                    } else if (json == "like_sent:") {
                        MessageData(text = "like_sent:")
                    }
                    else if (json.startsWith("got_match:")) {
                        val recipientId = json.removePrefix("got_match:")
                        MessageData(
                            text = "got_match:",
                            recipientUserId = recipientId
                        )
                    } else if (json.startsWith("deliveredMessage:")) {
                        val recipientId = json.removePrefix("deliveredMessage:")
                        MessageData(
                            recipientUserId = recipientId,
                            text = "deliveredMessage:"
                        )
                    } else if (json.startsWith("readMessages:")) {
                        val recipientId = json.removePrefix("readMessages:")

                        MessageData(
                            recipientUserId = recipientId,
                            text = "readMessages:"
                        )
                    }
                    else if (json.startsWith("no_longer_matched:")) {
                        MessageData(
                            text = "no_longer_matched:"
                        )
                    }
                    else if (json.startsWith("triggerOnline:")) {

                        val recipientUserId = json.substringAfter(":").take(36)

                        val recipientOnlineStatus =
                            json.substringAfter(recipientUserId).take(4).toBoolean()

                        val recipientLastSeen =
                            json.substringAfter(recipientUserId)
                                .substringAfter(recipientOnlineStatus.toString())
                                .toLong()

                        MessageData(
                            text = "triggerOnline:",
                            recipientUserId = recipientUserId,
                            recipientOnlineStatus = recipientOnlineStatus,
                            recipientLastSeen = recipientLastSeen
                        )
                    } else {
                        val messageDto = Json.decodeFromString<MessageDataDto>(json)
                        messageDto.toMessageData()
                    }
                } ?: flow { }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }


    override suspend fun closeSession() {
        try {
            socket?.close()
        } catch (e: Exception) {
            Log.d("Bober_error", "${e.message}")
        }

    }
}
