package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.responses.GetLastMessagesResponse
import com.zaurh.bober.data.responses.UnreadMessagesSizeResponse
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.domain.repository.MessageRepository
import com.zaurh.bober.preferences.BoberPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MessagesRepoImpl @Inject constructor(
    private val api: MessageApi,
    private val boberPreferences: BoberPreferences
) : MessageRepository {

    private val _chatList = MutableStateFlow<List<ChatData?>>(listOf())
    override val chatList: StateFlow<List<ChatData?>>
        get() = _chatList


    suspend fun getToken(): String? {
        return boberPreferences.getJwtKey.first()
    }


    override suspend fun getLastMessages(): GetLastMessagesResponse {
        return try {
            val token = getToken()

            val response = api.getLastMessages(
                token = "Bearer $token"
            )

            GetLastMessagesResponse(
                success = response.success,
                lastMessages = response.lastMessages,
                message = response.message
            )


        } catch (e: Exception) {
            GetLastMessagesResponse(
                success = false,
                message = e.message.toString()
            )
        }
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
        return try {
            val token = getToken()

            val response = api.getRecipientMessages(
                token = "Bearer $token",
                recipientUserId = recipientUserId,
                page = page,
                pageSize = pageSize
            )

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

//            if (response.success) {
//                _chatList.value = response.chatList
//            }

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


}
