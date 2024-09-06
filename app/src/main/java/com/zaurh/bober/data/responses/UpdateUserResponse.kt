package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.UserData

data class UpdateUserResponse(
    val success: Boolean,
    val user: UserData? = null,
    val message: String = "No message."
)
