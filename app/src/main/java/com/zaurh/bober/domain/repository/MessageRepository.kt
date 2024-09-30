package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.responses.ChatListResponse
import com.zaurh.bober.data.responses.GetAllMessagesResponse
import com.zaurh.bober.data.user.ChatData
import kotlinx.coroutines.flow.StateFlow

interface MessageRepository {

    val chatList: StateFlow<List<ChatData?>>

    suspend fun getPrivateMessages(): GetAllMessagesResponse
    suspend fun getChatList(): ChatListResponse

}