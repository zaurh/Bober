package com.zaurh.bober.screen.sign_up.states

data class UsernameState(
    val username: String = "",
    val errorMessage: String = "",
    val loading: Boolean = false
)
