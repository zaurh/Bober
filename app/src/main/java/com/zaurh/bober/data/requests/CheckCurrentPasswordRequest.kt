package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CheckCurrentPasswordRequest(
    val password: String
)