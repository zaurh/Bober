package com.zaurh.bober.data.message

import kotlinx.serialization.Serializable

@Serializable
data class EmoteData(
    val emoteSealed: EmoteSealed,
    val senderId: String,
    val senderUsername: String,
    val senderImage: String
)
