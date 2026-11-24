package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable
import com.zaurh.bober.data.user.UserData

@Serializable
data class GetUserDataResponse(
    val success: Boolean,
    val user: UserData? = null,
    val message: String = "No message.",
)