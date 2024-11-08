package com.zaurh.bober.data.responses

import com.zaurh.bober.data.message.LastMessageData
import kotlinx.serialization.Serializable

@Serializable
data class GetLastMessagesResponse(
    val success: Boolean,
    val lastMessages: List<LastMessageData> = listOf(),
    val message: String = "No message"
)