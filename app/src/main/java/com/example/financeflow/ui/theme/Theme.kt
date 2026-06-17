package com.example.financeflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreenPrimaryDark,
    secondary = CoolBlueSecondaryDark,
    tertiary = DeepSlateTertiaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ExpenseCrimson,
    onPrimary = BackgroundDark,
    onSecondary = BackgroundDark,
    onTertiary = BackgroundDark,
    onBackground = BackgroundLight,
    onSurface = BackgroundLight
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreenPrimary,
    secondary = CoolBlueSecondary,
    tertiary = DeepSlateTertiary,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = ExpenseCrimson,
    onPrimary = SurfaceLight,
    onSecondary = SurfaceLight,
    onTertiary = SurfaceLight,
    onBackground = DeepSlateTertiary,
    onSurface = DeepSlateTertiary
)

@Composable
fun FinanceFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor default to false so that our premium custom branding takes precedence
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamic color is intentionally bypassed by default to preserve custom branding
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
