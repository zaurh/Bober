package com.zaurh.bober.model

import com.zaurh.bober.data.message.EmoteData
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.data.message.room.MessageEntity
import kotlinx.serialization.Serializable

@Serializable
data class MessageData(
    val id: String? = null,
    val text: String? = null,
    val senderUsername: String? = null,
    val senderUserId: String? = null,
    val recipientUserId: String? = null,
    val replyingMessageId: String? = null,
    val emoted: List<EmoteData>? = listOf(),
    val timestamp: String? = null,
    var status: MessageStatus = MessageStatus.SENT,

    val recipientUsername: String? = null,
    val recipientOnlineStatus: Boolean? = null,
    val recipientLastSeen: Long? = null
)

fun MessageData.toMessageEntity() = MessageEntity(
    messageId = id,
    text = text,
    senderUserId = senderUserId,
    recipientUserId = recipientUserId,
    status = status,
    timeStamp = timestamp
)
