package com.zaurh.bober.data.message

import android.annotation.SuppressLint
import com.zaurh.bober.data.remote.FlexibleLongSerializer
import com.zaurh.bober.model.MessageData
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/** A chat message exactly as the backend sends it (`/messages` and chat socket frames). */
@Serializable
data class MessageDataDto(
    val id: String? = null,
    val text: String = "",
    val senderUsername: String = "",
    val senderUserId: String = "",
    val recipientUserId: String = "",
    val recipientUsername: String = "",
    val replyingMessageId: String = "",
    @Serializable(with = FlexibleLongSerializer::class)
    val timestamp: Long = 0,
    val status: MessageStatus = MessageStatus.SENT,
) {
    fun toMessageData(): MessageData = MessageData(
        id = id,
        text = text,
        timestamp = formatTimestampToTime(timestamp),
        status = status,
        senderUsername = senderUsername,
        senderUserId = senderUserId,
        recipientUserId = recipientUserId,
        recipientUsername = recipientUsername,
    )
}

@Serializable
enum class MessageStatus {
    SENT,       // Stored on the server, not yet received by the recipient
    DELIVERED,  // Reached the recipient's device
    READ,       // Read by the recipient
    FAILED      // Client-side only: could not be sent
}

@SuppressLint("SimpleDateFormat")
fun formatTimestampToTime(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("HH:mm")
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}
