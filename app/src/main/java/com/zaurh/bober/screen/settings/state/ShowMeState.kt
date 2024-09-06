package com.zaurh.bober.screen.settings.state

import com.zaurh.bober.data.user.ShowMe

data class ShowMeState(
    val showMe: ShowMe = ShowMe.EVERYONE,
    val state: Boolean = false,
    val loading: Boolean = false,
    val error: String = ""
)
