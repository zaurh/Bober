package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable
@Serializable
data class AddMediaResponse(
    val success: Boolean,
    val fileLink: String = "",
    val message: String = "No message.",
)