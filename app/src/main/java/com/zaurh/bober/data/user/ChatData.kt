package com.zaurh.bober.data.user

import kotlinx.serialization.Serializable

@Serializable
data class ChatData(
    val userId: String,
    val username: String,
    val lastUpdated: Long
)
