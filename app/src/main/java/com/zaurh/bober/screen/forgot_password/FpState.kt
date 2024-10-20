package com.zaurh.bober.screen.forgot_password

data class FpState(
    val emailText: String = "",
    val emailError: Boolean = false,
    val requestLoading: Boolean = false,
    val requestError: Boolean = false,
    val requestSuccess: Boolean = false,
    val message: String = ""
)
