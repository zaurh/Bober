package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
data class ChatData(
    val recipientId: String,
    val recipientImage: String,
    val recipientUsername: String,
    val lastUpdated: Long
)