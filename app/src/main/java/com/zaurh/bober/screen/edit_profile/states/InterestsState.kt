package com.zaurh.bober.screen.edit_profile.states

import com.zaurh.bober.data.user.Interests

data class InterestsState(
    val selectedInterests: List<Interests> = listOf(),
    val modalState: Boolean = false

)
