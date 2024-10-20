package com.zaurh.bober.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val userId: String,
    val password: String,
    val confirmPassword: String
)
