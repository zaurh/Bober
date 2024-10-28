package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class PrivMessagesRequest(
    val recipientUserId: String
)
