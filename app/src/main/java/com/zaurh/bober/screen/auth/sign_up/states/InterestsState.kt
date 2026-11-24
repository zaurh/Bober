package com.zaurh.bober.screen.auth.sign_up.states

import com.zaurh.bober.data.user.Interests

data class InterestsState(
    val interests: List<Interests> = listOf()
)
