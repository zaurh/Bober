package com.zaurh.bober.screen.sign_up.states

import com.zaurh.bober.data.user.ShowMe

data class ShowMeState(
    val showMe: ShowMe = ShowMe.EVERYONE,
    val showMeDropdown: Boolean = false
)
