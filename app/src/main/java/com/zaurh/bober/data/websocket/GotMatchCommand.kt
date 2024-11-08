package com.zaurh.bober.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class GotMatchCommand(
    val senderUserId: String,
    val senderImage: List<String>,
    val senderUsername: String,
    val recipientUserId: String,
    val recipientImage: List<String>,
    val recipientUsername: String
)
