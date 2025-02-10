package com.zaurh.bober.data.message

import android.annotation.SuppressLint
import com.zaurh.bober.model.MessageData
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Serializable
data class MessageDataDto(
    val text: String? = null,
    val senderUsername: String? = null,
    val senderUserId: String? = null,
    val recipientUsername: String? = null,
    val recipientUserId: String? = null,
    val replyingMessageId: String? = null,
    val emoted: List<EmoteData>,
    val timestamp: Long? = null,
    var status: MessageStatus = MessageStatus.SENT,
    val id: String? = null,
){
    fun toMessageData(): MessageData {
        return MessageData(
            id = id ?: "",
            text = text ?: "",
            timestamp = timestamp.toString() ?: "",
            status = status ?: MessageStatus.SENT,
            senderUsername = senderUsername ?: "",
            senderUserId = senderUserId ?: "",
            recipientUsername = recipientUsername ?: "",
            recipientUserId = recipientUserId ?: "",
            replyingMessageId = replyingMessageId ?: "",
            emoted = emoted ?: listOf()
        )
    }
}

enum class MessageStatus {
    SENT,       // Message was sent from the sender
    DELIVERED,  // Message was delivered to the recipient
    READ,       // Message was read by recipient
    FAILED,      // Message delivery failed
    SENDING
}

@SuppressLint("SimpleDateFormat")
fun formatTimestampToTime(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("HH:mm")
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}