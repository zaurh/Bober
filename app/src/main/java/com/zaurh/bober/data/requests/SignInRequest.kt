package com.zaurh.bober.data.requests


data class SignInRequest(
    val userId: String? = null,
    val username: String = "",
    val password: String
)