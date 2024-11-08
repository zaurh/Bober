package com.zaurh.bober.data.message

import kotlinx.serialization.Serializable

@Serializable
data class LastMessageData(
    val senderUserId: String,
    val recipientUserId: String,
    val messageText: String,
    val messageStatus: MessageStatus,
    val messageTime: String
)
