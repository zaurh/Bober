package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable
@Serializable
data class MessageResponse(
    val success: Boolean,
    val message: String = "Error message.",
)
