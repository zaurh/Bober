package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.ChatData
import kotlinx.serialization.Serializable

@Serializable
data class ChatListResponse(
    val success: Boolean,
    val chatList: List<ChatData>,
    val message: String = "No message.",
)
