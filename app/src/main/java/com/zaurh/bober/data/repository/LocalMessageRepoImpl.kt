package com.zaurh.bober.data.repository

import android.util.Log
import com.zaurh.bober.data.message.room.MessageDao
import com.zaurh.bober.data.message.room.MessageEntity
import com.zaurh.bober.domain.repository.LocalMessageRepository
import javax.inject.Inject

class LocalMessageRepoImpl @Inject constructor(
    private val dao: MessageDao
): LocalMessageRepository {
    override suspend fun insertMessage(message: MessageEntity) {
        dao.insertMessage(message)
        Log.d("dasdasdas", "$message")
    }

    override suspend fun getPendingMessages(): List<MessageEntity> {
        return dao.getMessages()
    }

    override suspend fun deleteMessage(text: String) {
        dao.deleteMessage(text)
    }

    override suspend fun deleteSentMessage() {
        dao.deleteSentMessage()
    }


}
