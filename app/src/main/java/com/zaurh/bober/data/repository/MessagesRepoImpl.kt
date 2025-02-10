package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.requests.ChangeChatBackgroundRequest
import com.zaurh.bober.data.requests.EmoteMessageRequest
import com.zaurh.bober.data.requests.RemoveEmoteRequest
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.EmotedMessageResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.MessageResponse
import com.zaurh.bober.data.responses.UnreadMessagesSizeResponse
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.preferences.BoberPreferences
import com.zaurh.bober.screen.chat.state.ChatListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MessagesRepoImpl @Inject constructor(
    private val api: MessageApi,
    private val boberPreferences: BoberPreferences
) : MessageRepository {


    private val _chatList = MutableStateFlow(ChatListState())
    override val chatList: StateFlow<ChatListState>
        get() = _chatList



    suspend fun getToken(): String? {
        return boberPreferences.getJwtKey.first()
    }


    override suspend fun getUnreadMessagesSize(): UnreadMessagesSizeResponse {
        return try {
            val token = getToken()

            val response = api.getUnreadMessagesSize(
                token = "Bearer $token"
            )

            UnreadMessagesSizeResponse(
                success = response.success,
                size = response.size
            )


        } catch (e: Exception) {
            UnreadMessagesSizeResponse(
                success = false,
                size = 0
            )
        }
    }

    override suspend fun getRecipientMessages(
        recipientUserId: String,
        page: Int,
        pageSize: Int
    ): GetAllMessagesResponse {
        Log.d("sadjdlaksda", recipientUserId)

        return try {
            val token = getToken()

            val response = api.getRecipientMessages(
                token = "Bearer $token",
                recipientUserId = recipientUserId,
                page = page,
                pageSize = pageSize
            )

            Log.d("sadjdlaksda", recipientUserId)

            Log.d("sadjdlaksda", "$response")

            GetAllMessagesResponse(
                success = response.success,
                messageList = response.messageList,
                message = response.message
            )


        } catch (e: Exception) {
            Log.d("sadjdlaksda", "response: ${e.message}")

            GetAllMessagesResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }

    override suspend fun getChatList(): ChatListResponse {
        return try {
            val token = getToken()

            val response = api.getChatList(
                token = "Bearer $token"
            )

            ChatListResponse(
                success = response.success,
                chatList = response.chatList,
                message = response.message
            )


        } catch (e: Exception) {
            ChatListResponse(
                success = false,
                chatList = listOf(),
                message = e.message.toString()
            )
        }
    }

    override suspend fun emoteMessage(
        senderUserId: String,
        recipientId: String,
        messageId: String,
        messageSenderId: String,
        emoteData: EmoteData
    ): EmotedMessageResponse {
        return try {
            val token = getToken()

            val response = api.emoteMessage(
                token = "Bearer $token",
                emoteMessageRequest = EmoteMessageRequest(
                    senderUserId = senderUserId,
                    recipientUserId = recipientId,
                    messageId = messageId,
                    messageSenderId = messageSenderId,
                    emoteData = emoteData
                )
            )

            EmotedMessageResponse(
                success = response.success,
                messageData = response.messageData,
                message = response.message
            )


        } catch (e: Exception) {
            EmotedMessageResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }

    override suspend fun changeChatBackground(
        recipientId: String,
        backgroundColor: BackgroundColor,
        doodle: Boolean
    ): MessageResponse {
        return try {
            val token = getToken()

            val response = api.changeChatBackground(
                token = "Bearer $token",
                chatBackgroundRequest = ChangeChatBackgroundRequest(
                    recipientId = recipientId,
                    backgroundColor = backgroundColor,
                    doodle = doodle
                )
            )

            MessageResponse(
                success = response.success,
                message = response.message
            )


        } catch (e: Exception) {
            MessageResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }

    override suspend fun removeEmote(
        senderUserId: String,
        recipientId: String,
        messageId: String
    ): EmotedMessageResponse {
        return try {
            val token = getToken()

            val response = api.removeEmote(
                token = "Bearer $token",
                removeEmoteRequest = RemoveEmoteRequest(
                    senderUserId = senderUserId,
                    recipientUserId = recipientId,
                    messageId = messageId
                )
            )

            EmotedMessageResponse(
                success = response.success,
                messageData = response.messageData,
                message = response.message
            )


        } catch (e: Exception) {
            EmotedMessageResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }


}
