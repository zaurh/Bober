package com.zaurh.bober.screen.sign_up.sign_up_image.image_edit.components

data class FilterState(
    val original: Boolean = false,
    val grayScale: Boolean = false,
    val sepia: Boolean = false,
    val invert: Boolean = false,
    val cool: Boolean = false,
    val warm: Boolean = false,
    val brightness: Boolean = false,
    val contrast: Boolean = false,
    val lomo: Boolean = false
)
