package com.zaurh.bober.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class NewLikeCommand(
    val senderUserId: String,
    val senderImage: List<String>,
    val senderUsername: String,
    val senderAge: Int,
    val recipientUserId: String,
)