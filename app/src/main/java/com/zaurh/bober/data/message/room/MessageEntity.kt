package com.zaurh.bober.data.message.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.model.MessageData

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val messageId: String?,
    val text: String?,
    val senderUserId: String?,
    val recipientUserId: String?,
    val status: MessageStatus,
    val timeStamp: String?
)

fun MessageEntity.toMessageData() = MessageData(
    id = messageId,
    text = text,
    senderUserId = senderUserId,
    recipientUserId = recipientUserId,
    status = status,
    timestamp = timeStamp
)
