package com.zaurh.bober.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val surface: Color,
    val textFieldContainer: Color,
    val buttonBackground: Color,
    val text: Color,
    val cursor: Color,
    val multipleChoice: Color
) {
    object Night : ThemeColors(
        surface = Color(0xFF212121),
        textFieldContainer =  Color(0xFF272727),
        buttonBackground = Color(0xFF272727),
        text = Color.White,
        cursor = Color.White,
        multipleChoice = Color(0xFF464646)
    )

    object Day : ThemeColors(
        surface = Color(0xFFEBFFF9),
        textFieldContainer = Color.White,
        buttonBackground = Color(0xFFFF8D9B),
        text = Color(0xFF1C274C),
        cursor = Color(0xFFFF8D9B),
        multipleChoice = Color(0xFFFF8D9B)
    )
}