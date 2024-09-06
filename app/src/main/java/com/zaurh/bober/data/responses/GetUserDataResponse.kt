package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.UserData

data class GetUserDataResponse(
    val success: Boolean,
    val user: UserData? = null,
    val message: String = "No message.",
)