package com.zaurh.bober.data.responses

import com.zaurh.bober.data.message.MessageDataDto

data class GetAllMessagesResponse(
    val success: Boolean,
    val messageList: List<MessageDataDto>? = listOf(),
    val message: String = "No message.",
)