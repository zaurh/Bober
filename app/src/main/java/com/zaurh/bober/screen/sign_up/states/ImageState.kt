package com.zaurh.bober.screen.sign_up.states

data class ImageState(
    val bottomSheetEnabled: Boolean = false,
    val pickedImage: String = "",
    val loading: Boolean = false,
    val errorMessage: String = ""
)
