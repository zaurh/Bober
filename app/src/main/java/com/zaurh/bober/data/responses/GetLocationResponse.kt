package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class GetLocationResponse(
    val success: Boolean,
    val distance: Int = 0,
    val message: String = ""
)
