package com.zaurh.bober.screen.chat.state

import com.zaurh.bober.model.MessageData

data class EmoteModalState(
    val enabled: Boolean = false,
    val messageData: MessageData? = null
)
