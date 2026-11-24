package com.zaurh.bober.screen.auth.sign_up.states

data class ImageState(
    val bottomSheetEnabled: Boolean = false,
    val pickedImage: String = "",
    val loading: Boolean = false,
    val errorMessage: String = ""
)
