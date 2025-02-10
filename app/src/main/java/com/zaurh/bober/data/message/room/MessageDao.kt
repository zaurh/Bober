package com.zaurh.bober.data.message.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zaurh.bober.data.message.MessageStatus

@Dao
interface MessageDao {
    @Insert()
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages")
    suspend fun getMessages(): List<MessageEntity>

    @Query("DELETE FROM messages WHERE text = :text")
    suspend fun deleteMessage(text: String)

    @Query("DELETE FROM messages")
    suspend fun clearMessages()

    @Query("DELETE FROM messages WHERE status = :status")
    suspend fun deleteSentMessage(status: MessageStatus = MessageStatus.SENT)

}