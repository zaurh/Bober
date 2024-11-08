package com.zaurh.bober.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class UnlikeCommand(
    val senderUserId: String,
    val recipientUserId: String,
)