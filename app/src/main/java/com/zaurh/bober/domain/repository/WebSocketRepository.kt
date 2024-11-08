package com.zaurh.bober.domain.repository

import com.zaurh.bober.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WebSocketRepository {

    val switchedRecipient: StateFlow<String>


    suspend fun isConnected(): Boolean

    suspend fun initSession(): Resource<Unit>

    suspend fun switchRecipient(newRecipientUserId: String)

    suspend fun typingMessage(): Resource<Unit>

//    suspend fun messageIsDelivered(): Resource<Unit>

    suspend fun messageIsRead(): Resource<Unit>

    suspend fun triggerOnline(): Resource<Unit>

    suspend fun sendMessage(message: String): Resource<Unit>

    suspend fun sendLike(profileId: String): Resource<Unit>

    fun observeMessages(): Flow<Any>

    suspend fun closeSession()

}