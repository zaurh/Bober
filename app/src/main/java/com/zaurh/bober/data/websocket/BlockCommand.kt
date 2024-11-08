package com.zaurh.bober.data.websocket

import kotlinx.serialization.Serializable

@Serializable
data class BlockCommand(
    val senderUserId: String,
    val recipientUserId: String,
)