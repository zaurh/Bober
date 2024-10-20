package com.zaurh.bober.screen.settings.account_center.email.state

data class EmailValidationState(
    val loading: Boolean = false,
    val message: String = "",
    val success: Boolean = false,
    val email: String = ""
)
