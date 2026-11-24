package com.zaurh.bober.data.responses

import com.zaurh.bober.data.message.MessageDataDto
import kotlinx.serialization.Serializable

@Serializable
data class EmotedMessageResponse(
    val success: Boolean,
    val messageData: MessageDataDto? = null,
    val message: String = "No message.",
)
