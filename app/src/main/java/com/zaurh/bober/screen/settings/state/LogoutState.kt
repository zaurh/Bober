package com.zaurh.bober.screen.settings.state

data class LogoutState(
    val success: Boolean = false,
    val loading: Boolean = false,
    val error: String = ""
)
