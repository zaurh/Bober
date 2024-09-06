package com.zaurh.bober.screen.chat

import com.zaurh.bober.model.MessageData

data class ChatState(
    val messages: List<MessageData> = emptyList(),
    val recipientUserId: String? = null,
    val recipientOnlineStatus: Boolean? = null,
    val recipientLastSeen: Long? = null,
    val recipientIsTyping: Boolean? = null,
    val isLoading: Boolean = false
)
