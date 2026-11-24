package com.zaurh.bober.data.repository

import com.zaurh.bober.data.message.MessageApi
import com.zaurh.bober.data.remote.apiCall
import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.user.ChatData
import com.zaurh.bober.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepoImpl @Inject constructor(
    private val api: MessageApi
) : MessageRepository {

    private val _chatList = MutableStateFlow<List<ChatData?>>(listOf())
    override val chatList: StateFlow<List<ChatData?>> get() = _chatList

    override suspend fun getPrivateMessages(): GetAllMessagesResponse =
        apiCall({ GetAllMessagesResponse(success = false, message = it) }) {
            api.getPrivateMessages()
        }

    override suspend fun getChatList(): ChatListResponse =
        apiCall({ ChatListResponse(success = false, message = it) }) {
            api.getChatList().also { if (it.success) _chatList.value = it.chatList }
        }

    override fun clear() {
        _chatList.value = listOf()
    }
}
