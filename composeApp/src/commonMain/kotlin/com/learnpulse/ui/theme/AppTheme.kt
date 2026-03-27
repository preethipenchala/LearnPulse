package com.learnpulse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val LightColorScheme = lightColorScheme(
    primary = LearnPulsePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = GradientEnd,
    secondary = LearnPulseSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = NeutralBackground,
    onBackground = NeutralTextPrimary,
    surface = NeutralSurface,
    onSurface = NeutralTextPrimary,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF0F0F5),
    error = ErrorColor,
    onError = androidx.compose.ui.graphics.Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = GradientEnd,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LearnPulsePrimary,
    secondary = LearnPulseSecondary,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurface2,
    error = ErrorColor,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun LearnPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LearnPulseTypography,
            content = content
        )
    }
}

object LearnPulseTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current
}
