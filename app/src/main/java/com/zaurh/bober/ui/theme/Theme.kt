package com.zaurh.bober.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    surface = ThemeColors.Night.surface,
    tertiary = ThemeColors.Night.textFieldContainer,
    secondary = ThemeColors.Night.buttonBackground,
    primary = ThemeColors.Night.text,
    onSecondary = ThemeColors.Night.cursor,
    onTertiary = ThemeColors.Night.multipleChoice
)

private val LightColorScheme = lightColorScheme(
    surface = ThemeColors.Day.surface,
    tertiary = ThemeColors.Day.textFieldContainer,
    secondary = ThemeColors.Day.buttonBackground,
    primary = ThemeColors.Day.text,
    onSecondary = ThemeColors.Day.cursor,
    onTertiary = ThemeColors.Day.multipleChoice


)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun BoberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colors = if (!darkTheme) {
        LightColorScheme
    } else {
        DarkColorScheme
    }
    val activity = LocalView.current.context as Activity
    val backgroundArgb = colors.surface.toArgb()
    activity.window?.statusBarColor = backgroundArgb

    val wic = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    wic.isAppearanceLightStatusBars = !darkTheme
    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = Typography
    )
}