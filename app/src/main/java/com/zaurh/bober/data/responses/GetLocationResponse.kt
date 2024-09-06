package com.zaurh.bober.data.responses


data class GetLocationResponse(
    val success: Boolean,
    val distance: Int = 0,
    val message: String = ""
)
