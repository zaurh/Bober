package com.zaurh.bober.data.responses

data class AddMediaResponse(
    val success: Boolean,
    val fileLink: String = "",
    val message: String = "No message.",
)