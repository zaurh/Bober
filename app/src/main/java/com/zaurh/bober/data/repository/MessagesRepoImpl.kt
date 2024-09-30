package com.zaurh.bober.data.repository

import android.content.SharedPreferences
import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MessagesRepoImpl @Inject constructor(
    private val api: MessageApi,
    private val prefs: SharedPreferences
) : MessageRepository {

    private val _chatList = MutableStateFlow<List<ChatData?>>(listOf())
    override val chatList: StateFlow<List<ChatData?>>
        get() = _chatList


    override suspend fun getPrivateMessages(): GetAllMessagesResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: ""

            val response = api.getPrivateMessages(
                token = "Bearer $token"
            )

            GetAllMessagesResponse(
                success = response.success,
                messageList = response.messageList,
                message = response.message
            )


        } catch (e: Exception) {
            GetAllMessagesResponse(
                success = false,
                message = e.message.toString()
            )
        }
    }

    override suspend fun getChatList(): ChatListResponse {
        return try {
            val token = prefs.getString("jwt", null) ?: ""

            val response = api.getChatList(
                token = "Bearer $token"
            )

            if (response.success) {
                _chatList.value = response.chatList
            }

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
