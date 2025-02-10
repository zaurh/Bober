package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class RemoveEmoteRequest(
    val senderUserId: String? = null,
    val recipientUserId: String? = null,
    val messageId: String? = null
)