package com.zaurh.bober.screen.chat.state

import com.zaurh.bober.model.MessageData

data class ChatState(
    val recipientMessages: List<MessageData>? = null,
    val recipientMessagesEmpty: Boolean = false,
    val recipientMessagesLoading: Boolean = false,

    val extendMessagesLoading: Boolean = false,
    val pageSize: Int = 20,

    val recipientUserId: String? = null,
    val recipientOnlineStatus: Boolean? = null,
    val recipientLastSeen: Long? = null,
    val recipientIsTyping: Boolean? = null,

    val recipientDataError: Boolean = false,

    val isLoading: Boolean = false,
    val socketIsObserving: Boolean = false,
    val internetClosed: Boolean = false,
    val initialized: Boolean = false,
    val switchedRecipientId: String = "",

    )
