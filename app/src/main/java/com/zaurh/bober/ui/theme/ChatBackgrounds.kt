package com.zaurh.bober.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.zaurh.bober.data.user.ChatBackground

/** The color drawn for a chat background, or null for [ChatBackground.DEFAULT] (the theme surface). */
val ChatBackground.color: Color?
    get() = when (this) {
        ChatBackground.DEFAULT -> null
        ChatBackground.PURPLE -> Color(0xFF800080)
        ChatBackground.PURPLE_LIGHT -> Color(0xFFDA70D6)
        ChatBackground.PURPLE_DARK -> Color(0xFF4B0082)
        ChatBackground.LAVENDER -> Color(0xFFE6E6FA)
        ChatBackground.VIOLET -> Color(0xFFEE82EE)
        ChatBackground.RED -> Color(0xFFFF0000)
        ChatBackground.RED_LIGHT -> Color(0xFFFFA07A)
        ChatBackground.RED_DARK -> Color(0xFF8B0000)
        ChatBackground.PINK -> Color(0xFFFFC0CB)
        ChatBackground.CRIMSON -> Color(0xFFDC143C)
        ChatBackground.YELLOW -> Color(0xFFFFFF00)
        ChatBackground.YELLOW_LIGHT -> Color(0xFFFFFFE0)
        ChatBackground.YELLOW_DARK -> Color(0xFFCCCC00)
        ChatBackground.GOLD -> Color(0xFFFFD700)
        ChatBackground.ORANGE -> Color(0xFFFFA500)
        ChatBackground.BLUE -> Color(0xFF0000FF)
        ChatBackground.BLUE_LIGHT -> Color(0xFFADD8E6)
        ChatBackground.BLUE_DARK -> Color(0xFF00008B)
        ChatBackground.SKY_BLUE -> Color(0xFF87CEEB)
        ChatBackground.NAVY -> Color(0xFF000080)
        ChatBackground.GREEN -> Color(0xFF008000)
        ChatBackground.GREEN_LIGHT -> Color(0xFF90EE90)
        ChatBackground.GREEN_DARK -> Color(0xFF006400)
        ChatBackground.MINT -> Color(0xFF98FF98)
        ChatBackground.LIME -> Color(0xFF00FF00)
        ChatBackground.GRAY -> Color(0xFF808080)
        ChatBackground.GRAY_LIGHT -> Color(0xFFD3D3D3)
        ChatBackground.GRAY_DARK -> Color(0xFFA9A9A9)
        ChatBackground.SILVER -> Color(0xFFC0C0C0)
        ChatBackground.CHARCOAL -> Color(0xFF36454F)
        ChatBackground.BROWN -> Color(0xFFA52A2A)
        ChatBackground.BROWN_LIGHT -> Color(0xFFDEB887)
        ChatBackground.BROWN_DARK -> Color(0xFF654321)
        ChatBackground.TAN -> Color(0xFFD2B48C)
        ChatBackground.BEIGE -> Color(0xFFF5F5DC)
        ChatBackground.WHITE -> Color(0xFFFFFFFF)
        ChatBackground.BLACK -> Color(0xFF000000)
        ChatBackground.IVORY -> Color(0xFFFFFFF0)
        ChatBackground.SNOW -> Color(0xFFFFFAFA)
        ChatBackground.COAL -> Color(0xFF2C2C2C)
    }

/** Readable color for text and patterns drawn directly on [background] (timestamps, doodle). */
fun contentColorOn(background: Color): Color =
    if (background.luminance() > 0.45f) Color.Black else Color.White
