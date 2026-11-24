package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenRequest(
    val token: String
)