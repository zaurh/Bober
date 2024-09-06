package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.UserData

data class SignUpResponse(
    val success: Boolean,
    val user: UserData? = null,
    val message: String? = null,
    val passwordError: String? = null,
    val imageError: String? = null
)
