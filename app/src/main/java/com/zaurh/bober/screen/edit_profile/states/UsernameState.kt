package com.zaurh.bober.screen.edit_profile.states

data class UsernameState(
    val text: String = "",
    val changeState: Boolean = false,
    val loading: Boolean = false,
    val error: String = ""
)
