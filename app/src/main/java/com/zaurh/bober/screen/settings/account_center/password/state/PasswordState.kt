package com.zaurh.bober.screen.settings.account_center.password.state

data class PasswordState(
    val currentPassword: String = "",
    val currentPasswordVisible: Boolean = false,
    val newPassword: String = "",
    val newPasswordVisible: Boolean = false,

    val updatePasswordLoading: Boolean = false,
    val updatePasswordMessage: String = "",
    val updatePasswordSuccess: Boolean = false,

    val resetPasswordLoading: Boolean = false,
    val resetPasswordMessage: String = "",
    val resetPasswordSuccess: Boolean = false
)
