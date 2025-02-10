package com.zaurh.bober.data.requests

import com.zaurh.bober.data.user.BackgroundColor
import kotlinx.serialization.Serializable

@Serializable
data class ChangeChatBackgroundRequest(
    val recipientId: String,
    val backgroundColor: BackgroundColor,
    val doodle: Boolean
)