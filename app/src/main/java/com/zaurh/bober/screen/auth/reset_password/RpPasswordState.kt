package com.zaurh.bober.screen.auth.reset_password

data class RpPasswordState(
    val newPassword: String = "",
    val newPasswordVisible: Boolean = false,
    val newPasswordAgain: String = "",
    val newPasswordAgainVisible: Boolean = false,
    val error: Boolean = false,
    val updatePasswordLoading: Boolean = false,
    val updatePasswordMessage: String = "",
    val validateTokenLoading: Boolean = false,
    val validationSuccess: Boolean = false
)
