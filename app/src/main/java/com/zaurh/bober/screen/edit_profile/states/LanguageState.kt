package com.zaurh.bober.screen.edit_profile.states

import com.zaurh.bober.data.user.Languages

data class LanguageState(
    val selectedLanguages: List<Languages> = listOf(),
    val modalState: Boolean = false

)
