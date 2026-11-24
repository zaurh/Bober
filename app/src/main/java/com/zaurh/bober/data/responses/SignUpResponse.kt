package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable
import com.zaurh.bober.data.user.UserData

@Serializable
data class SignUpResponse(
    val success: Boolean,
    val user: UserData? = null,
    val message: String? = null,
    val passwordError: String? = null,
    val imageError: String? = null
)
