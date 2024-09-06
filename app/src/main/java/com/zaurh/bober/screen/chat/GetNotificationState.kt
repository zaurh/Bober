package com.zaurh.bober.screen.chat

data class GetNotificationState(
    val onChatScreen: Boolean? = false,
    val recipientId: String? = ""
)
