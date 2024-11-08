package com.zaurh.bober.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class UnMatchCommand(
    val senderUserId: String,
    val recipientUserId: String,
)