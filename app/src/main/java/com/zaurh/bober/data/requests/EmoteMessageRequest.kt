package com.zaurh.bober.data.requests

import com.zaurh.bober.data.message.EmoteData
import kotlinx.serialization.Serializable

@Serializable
data class EmoteMessageRequest(
    val senderUserId: String? = null,
    val recipientUserId: String? = null,
    val messageId: String? = null,
    val messageSenderId: String? = null,
    val emoteData: EmoteData
)
