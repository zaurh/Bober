package com.zaurh.bober.screen.sign_up.states

import com.zaurh.bober.data.user.Gender

data class GenderState(
    val selectedGender: Gender = Gender.PREFER_NOT_TO_SAY
)
