package com.zaurh.bober.data.responses

import com.zaurh.bober.model.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class EmotedMessageResponse(
    val success: Boolean,
    val messageData: MessageData? = null,
    val message: String = "No message.",
)