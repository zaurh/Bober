package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable
import com.zaurh.bober.data.message.MessageDataDto

@Serializable
data class GetAllMessagesResponse(
    val success: Boolean,
    val messageList: List<MessageDataDto> = listOf(),
    val message: String = "No message.",
)