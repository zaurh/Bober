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
    val recipientUserId: String? = null,
    val timestamp: Long? = null,
    var status: MessageStatus = MessageStatus.SENT,
    var deliveredTimestamp: Long? = null,
    val id: String? = null,
){
    fun toMessageData(): MessageData {
//        val formattedDate = formatTimestampToTime(timestamp ?: 0)

        return MessageData(
            id = id,
            text = text,
            timestamp = timestamp.toString(),
            status = status,
            deliveredTimestamp = deliveredTimestamp,
            senderUsername = senderUsername,
            senderUserId = senderUserId,
            recipientUserId = recipientUserId
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