package com.zaurh.bober.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand palette. The purple→pink gradient and the beaver are Bober's identity; everything else is neutral.
object BoberPalette {
    val Purple = Color(0xFFC13DFF)
    val Pink = Color(0xFFFF8D9B)
    val PinkStrong = Color(0xFFE5486F) // buttons/links: readable with white text
    val PinkLight = Color(0xFFFF9FB3)  // accent on dark surfaces
    val Navy = Color(0xFF1C274C)

    val Like = Color(0xFF2BC48A)
    val Pass = Color(0xFFFF5A6E)
    val Super = Color(0xFF7C5CFF)
    val Online = Color(0xFF2BC48A)
}

/** The signature gradient used on auth screens, the match overlay and brand accents. */
val BrandGradient: Brush = Brush.linearGradient(listOf(BoberPalette.Purple, BoberPalette.Pink))

/** Bottom scrim that keeps white text readable over photos. */
val PhotoScrim: Brush = Brush.verticalGradient(
    0f to Color.Transparent,
    0.55f to Color.Black.copy(alpha = 0.15f),
    1f to Color.Black.copy(alpha = 0.85f)
)

internal object LightTokens {
    val primary = BoberPalette.PinkStrong
    val onPrimary = Color.White
    val primaryContainer = Color(0xFFFFE0E7)
    val onPrimaryContainer = Color(0xFF5C0A24)
    val secondary = Color(0xFF9C3DE0)
    val onSecondary = Color.White
    val secondaryContainer = Color(0xFFF3E1FF)
    val onSecondaryContainer = Color(0xFF3A0A5C)
    val background = Color(0xFFFFF8FA)
    val onBackground = BoberPalette.Navy
    val surface = Color(0xFFFFF8FA)
    val onSurface = BoberPalette.Navy
    val surfaceVariant = Color(0xFFF4E9EE)
    val onSurfaceVariant = Color(0xFF5E6682)
    val surfaceContainerLowest = Color.White
    val surfaceContainerLow = Color(0xFFFFF1F5)
    val surfaceContainer = Color(0xFFFBEDF2)
    val surfaceContainerHigh = Color(0xFFF6E7ED)
    val surfaceContainerHighest = Color(0xFFF0E1E8)
    val outline = Color(0xFFD9C8D0)
    val outlineVariant = Color(0xFFEBDDE3)
    val error = Color(0xFFD32F45)
}

internal object DarkTokens {
    val primary = BoberPalette.PinkLight
    val onPrimary = Color(0xFF4A0019)
    val primaryContainer = Color(0xFF6E1F38)
    val onPrimaryContainer = Color(0xFFFFD9E1)
    val secondary = Color(0xFFD9A6FF)
    val onSecondary = Color(0xFF3A0A5C)
    val secondaryContainer = Color(0xFF52237A)
    val onSecondaryContainer = Color(0xFFF3E1FF)
    val background = Color(0xFF141118)
    val onBackground = Color(0xFFF2ECF4)
    val surface = Color(0xFF141118)
    val onSurface = Color(0xFFF2ECF4)
    val surfaceVariant = Color(0xFF2C2632)
    val onSurfaceVariant = Color(0xFFB4ABBD)
    val surfaceContainerLowest = Color(0xFF0F0C12)
    val surfaceContainerLow = Color(0xFF1A161E)
    val surfaceContainer = Color(0xFF1F1A24)
    val surfaceContainerHigh = Color(0xFF29232F)
    val surfaceContainerHighest = Color(0xFF342D3A)
    val outline = Color(0xFF5A5161)
    val outlineVariant = Color(0xFF3A333F)
    val error = Color(0xFFFF8A95)
}
