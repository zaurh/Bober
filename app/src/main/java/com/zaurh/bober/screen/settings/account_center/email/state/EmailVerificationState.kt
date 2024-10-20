package com.zaurh.bober.screen.settings.account_center.email.state

data class EmailVerificationState(
    val loading: Boolean = false,
    val message: String = "",
    val success: Boolean = false
)
