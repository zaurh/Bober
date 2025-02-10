package com.zaurh.bober.screen.chat.state

data class ReplyingState(
    val enabled: Boolean = false,
    val username: String = "",
    val messageText: String = "",
    val messageId: String = "",
    val replyingMessageTriggered: Boolean = false,
    val triggeredMessageId: String = ""
)
