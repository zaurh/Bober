package com.zaurh.bober.screen.sign_up.states

data class PasswordState(
    val password: String = "",
    val passwordVisibility: Boolean = false,
    val errorMessage: String = ""
)
