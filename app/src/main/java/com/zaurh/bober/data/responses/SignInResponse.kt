package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.UserData

data class SignInResponse(
    val success: Boolean,
    val user: UserData? = null,
    val token: String? = null,
    val message: String = "No message.",
)
