package com.zaurh.bober.screen.auth.sign_up.states

data class PasswordState(
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val errorMessage: String = ""
)
