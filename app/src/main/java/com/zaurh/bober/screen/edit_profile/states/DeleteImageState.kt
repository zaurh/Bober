package com.zaurh.bober.screen.edit_profile.states

data class DeleteImageState(
    val selectedImage: String = "",
    val cannotDelete: Boolean = false,
    val confirmation: Boolean = false,
    val loading: Boolean = false
)
