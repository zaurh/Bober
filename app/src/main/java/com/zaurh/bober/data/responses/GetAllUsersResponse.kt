package com.zaurh.bober.data.responses

import com.zaurh.bober.data.user.UserData

data class GetAllUsersResponse(
    val success: Boolean,
    val userList: List<UserData> = listOf(),
    val message: String = "No message.",
)