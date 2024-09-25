package com.zaurh.bober.domain.repository

import com.zaurh.bober.model.MessageData
import com.zaurh.bober.util.Resource
import kotlinx.coroutines.flow.Flow

interface WebSocketRepository {

    suspend fun isConnected(): Boolean

    suspend fun initSession(): Resource<Unit>

    suspend fun switchRecipient(newRecipientUserId: String)

    suspend fun typingMessage(): Resource<Unit>

    suspend fun messageIsDelivered(messageId: String): Resource<Unit>

    suspend fun messageIsRead(messageId: String): Resource<Unit>

    suspend fun triggerOnline(): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    suspend fun sendLike(profileId: String): Resource<Unit>

    fun observeMessages(): Flow<MessageData>

    suspend fun closeSession()

}