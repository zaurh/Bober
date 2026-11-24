package com.zaurh.bober.model

import com.zaurh.bober.data.message.MessageStatus

/** A chat message as shown in the UI. */
data class MessageData(
    val id: String? = null,
    val text: String? = null,
    val senderUsername: String? = null,
    val senderUserId: String? = null,
    val recipientUserId: String? = null,
    val recipientUsername: String? = null,
    val timestamp: String? = null,
    val status: MessageStatus = MessageStatus.SENT,
)
