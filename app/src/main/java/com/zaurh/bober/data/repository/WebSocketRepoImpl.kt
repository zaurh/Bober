package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.message.MessageDataDto
import com.zaurh.bober.data.websocket.BlockCommand
import com.zaurh.bober.data.websocket.GotMatchCommand
import com.zaurh.bober.data.websocket.NewLikeCommand
import com.zaurh.bober.data.websocket.UnMatchCommand
import com.zaurh.bober.data.websocket.UnlikeCommand
import com.zaurh.bober.domain.repository.LocalMessageRepository
import com.zaurh.bober.domain.repository.WebSocketRepository
import com.zaurh.bober.model.MessageData
import com.zaurh.bober.preferences.BoberPreferences
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WebSocketRepoImpl @Inject constructor(
    private val client: HttpClient,
    private val boberPreferences: BoberPreferences,
    private val localMessageRepository: LocalMessageRepository
) : WebSocketRepository {

    private var socket: WebSocketSession? = null
    private var isConnected: Boolean = false // Connection state

    private val _switchedRecipient = MutableStateFlow("")
    override val switchedRecipient: StateFlow<String> get() = _switchedRecipient

    override suspend fun isConnected(): Boolean {
        return isConnected
    }

    suspend fun getToken(): String? {
        return boberPreferences.getJwtKey.first()
    }

    override suspend fun initSession(): Resource<Unit> {
        return try {
            Log.d("websocketconnectionlog2", "${socket?.isActive}")
            // Check if the socket is inactive or null before attempting to create a new session.
            if (socket == null || socket?.isActive == false) {
                Log.d("websocketconnectionlog", "Initializing new WebSocket session.")
                socket?.close() // Close any existing session (this may be redundant)
                socket = null // Reset to ensure reinitialization
                val token = getToken()
//                Log.d("websocketconnectionlog", "${client.webSocketSession()}")

                socket = client.webSocketSession {
                    url(WEBSOCKET_URL)
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                Log.d("websocketconnectionlog", "$socket")

                // Update connection state
                isConnected = socket?.isActive == true
            }

            if (isConnected) {
                Log.d("websocketconnectionlog", "WebSocket connected and active.")
                Resource.Success(Unit)
            } else {
                Log.d("websocketconnectionlog", "WebSocket connection failed or inactive.")
                Resource.Error("Couldn't establish a connection.")
            }
        } catch (e: Exception) {
            Log.d("websocketconnectionlog", "Error during WebSocket session initialization: ${e.message}")
            isConnected = false // Update connection state on error
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
                    _switchedRecipient.value = newRecipientUserId
                }
                Log.d("switchRecipient", "$newRecipientUserId")

            }
        } catch (e: Exception) {
            Log.d("websocketBober", "switcherror: ${e.message}...")
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

//    override suspend fun messageIsDelivered(): Resource<Unit> {
//        return try {
//            socket?.let {
//                if (it.isActive) {
//                    it.send("deliveredMessage:")
//                }
//            }
//            Resource.Success(Unit)
//        } catch (e: Exception) {
//            Resource.Error(message = e.message ?: "")
//        }
//    }

    override suspend fun messageIsRead(): Resource<Unit> {
        return try {
            socket?.let {
                if (it.isActive) {
                    it.send("readMessages:")
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
                Log.d("websocketBober", "sendMessage start: ${System.currentTimeMillis()}")
                socket?.send(Frame.Text(message))
                localMessageRepository.deleteMessage(message)
                Log.d("websocketBober", "sendMessage end: ${System.currentTimeMillis()}")
                Resource.Success(Unit)
            } else {
                Log.d("websocketBober", "sendmessage socket is not active...")
                Resource.Error("Socket is not active")
            }
        } catch (e: Exception) {
            Log.d("websocketBober", "sendmessage: ${e.message}...")

            Resource.Error("Error: ${e.message}")
        }
    }

    override suspend fun sendLike(profileId: String): Resource<Unit> {
        return try {
            if (socket?.isActive == true) {
//                socket?.send(Frame.Text("like_sent:$profileId"))
                Resource.Success(Unit)
            } else {
                Resource.Error("Socket is not active")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }


    override fun observeMessages(): Flow<MessageEvent> {
        return flow {
            try {
                // Verify that the socket is active and ready to receive messages
                val isSocketActive = socket?.isActive == true
                Log.d("websocketBober", "Socket active: $isSocketActive")

                if (!isSocketActive) {
                    Log.e("websocketBober", "Socket is not active")
                    return@flow // Exit if the socket is not active
                }

                Log.d("websocketBober", "soket : ${socket?.incoming}")


                socket?.incoming
                    ?.receiveAsFlow()
                    ?.filterIsInstance<Frame.Text>()
                    ?.collect { frame ->
                        val json = frame.readText()
                        Log.d("websocketBober", "Received JSON: $json")

                        // Handle each type of message event based on the content of the JSON
                        val event = when {
                            json.startsWith("Switched recipient to") -> MessageEvent.SwitchedRecipient("switched to:")
                            json.startsWith("typing:") -> {
                                val senderUserId = json.removePrefix("typing:")
                                MessageEvent.Typing(senderUserId)
                            }
                            json.startsWith("like_sent:") -> {
                                val jsonData = json.removePrefix("like_sent:")
                                try {
                                    val newLikeCommand = Json.decodeFromString<NewLikeCommand>(jsonData)
                                    MessageEvent.LikeSent(newLikeCommand)
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                            json.startsWith("unlike_sent:") -> {
                                val jsonData = json.removePrefix("unlike_sent:")
                                try {
                                    val unlikeCommand = Json.decodeFromString<UnlikeCommand>(jsonData)
                                    MessageEvent.UnlikeSent(unlikeCommand)
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                            json.startsWith("unmatch_sent:") -> {
                                val jsonData = json.removePrefix("unmatch_sent:")
                                try {
                                    val unMatchCommand = Json.decodeFromString<UnMatchCommand>(jsonData)
                                    MessageEvent.UnMatchSent(unMatchCommand)
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                            json.startsWith("block_sent:") -> {
                                val jsonData = json.removePrefix("block_sent:")
                                try {
                                    val blockCommand = Json.decodeFromString<BlockCommand>(jsonData)
                                    MessageEvent.BlockSent(blockCommand)
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                            json.startsWith("sent_emote:") -> {
                                val encodedMessageData = json.removePrefix("sent_emote:")
                                try {
                                    val decodedMessageData = Json.decodeFromString<MessageData>(encodedMessageData)
                                    MessageEvent.EmoteMessage(
                                        messageData = decodedMessageData
                                    )
                                }catch (e: Exception){
                                    return@collect
                                }


                            }
                            json.startsWith("remove_emote:") -> {
                                val encodedMessageData = json.removePrefix("remove_emote:")
                                try {
                                    val decodedMessageData = Json.decodeFromString<MessageData>(encodedMessageData)
                                    MessageEvent.RemoveEmote(
                                        messageData = decodedMessageData
                                    )
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                            json.startsWith("got_match:") -> {
                                val jsonData = json.removePrefix("got_match:")
                                try {
                                    val gotMatchCommand = Json.decodeFromString<GotMatchCommand>(jsonData)
                                    MessageEvent.GotMatch(gotMatchCommand)
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                            json.startsWith("deliveredMessage:") -> {
                                val recipientId = json.removePrefix("deliveredMessage:")
                                MessageEvent.DeliveredMessage(
                                    messageData = MessageData(
                                        text = "deliveredMessage:",
                                        recipientUserId = recipientId
                                    )
                                )
                            }
                            json.startsWith("readMessages:") -> {
                                val recipientAndSenderId = json.removePrefix("readMessages:")
                                val senderId = recipientAndSenderId.take(36)
                                val recipientId = recipientAndSenderId.takeLast(36)
                                MessageEvent.ReadMessages(
                                    messageData = MessageData(
                                        text = "readMessages:",
                                        senderUserId = senderId,
                                        recipientUserId = recipientId
                                    )
                                )
                            }
                            json.startsWith("no_longer_matched:") -> MessageEvent.NoLongerMatched
                            json.startsWith("triggerOnline:") -> {
                                val recipientUserId = json.substringAfter(":").take(36)
                                val recipientOnlineStatus = json.substringAfter(recipientUserId).take(4).toBoolean()
                                val recipientLastSeen = json.substringAfter(recipientUserId)
                                    .substringAfter(recipientOnlineStatus.toString())
                                    .toLong()
                                MessageEvent.TriggerOnline(
                                    recipientUserId,
                                    recipientOnlineStatus,
                                    recipientLastSeen
                                )
                            }
                            else -> {
                                try {
                                    val messageDto = Json.decodeFromString<MessageDataDto>(json)
                                    MessageEvent.GenericMessage(messageDto.toMessageData())
                                }catch (e: Exception){
                                    return@collect
                                }

                            }
                        }
                        emit(event) // Emit the parsed message event to the flow
                    }
            } catch (e: Exception) {
                Log.e("websocketBober", "Error observing messages: ${e.message}")
            }
        }
    }


    override suspend fun closeSession() {
        try {
            socket?.close()
            socket = null
            isConnected = false // Update connection state
        } catch (e: Exception) {
            Log.d("Bober_error", "${e.message}")
        }
    }
}



sealed class MessageEvent {
    data class SwitchedRecipient(val text: String) : MessageEvent()
    data class Typing(val senderUserId: String) : MessageEvent()
    data class LikeSent(val data: NewLikeCommand): MessageEvent()
    data class UnlikeSent(val data: UnlikeCommand): MessageEvent()
    data class UnMatchSent(val data: UnMatchCommand): MessageEvent()
    data class BlockSent(val data: BlockCommand): MessageEvent()
    data class GotMatch(val data: GotMatchCommand) : MessageEvent()
    data class DeliveredMessage(val messageData: MessageData) : MessageEvent()
    data class ReadMessages(val messageData: MessageData) : MessageEvent()
    object NoLongerMatched : MessageEvent()
    data class TriggerOnline(val recipientUserId: String, val onlineStatus: Boolean, val lastSeen: Long) : MessageEvent()
    data class GenericMessage(val messageData: MessageData) : MessageEvent()
    data class EmoteMessage(val messageData: MessageData) : MessageEvent()
    data class RemoveEmote(val messageData: MessageData) : MessageEvent()
}