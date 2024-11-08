package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class UnreadMessagesSizeResponse(
    val success: Boolean,
    val size: Int,
)