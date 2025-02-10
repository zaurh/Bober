package com.zaurh.bober.domain.repository

import com.zaurh.bober.data.message.room.MessageEntity

interface LocalMessageRepository {
    suspend fun insertMessage(message: MessageEntity)
    suspend fun getPendingMessages(): List<MessageEntity>
    suspend fun deleteMessage(text: String)
    suspend fun deleteSentMessage()
    suspend fun clearMessages()
}