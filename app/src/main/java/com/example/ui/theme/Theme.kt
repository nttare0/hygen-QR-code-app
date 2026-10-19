package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NeuColorScheme = lightColorScheme(
    primary = NeuAccentBlue,
    onPrimary = NeuTextWhite,
    primaryContainer = NeuAccentBlueLight,
    onPrimaryContainer = NeuTextWhite,
    secondary = NeuAccentOrange,
    onSecondary = NeuTextWhite,
    background = NeuBackground,
    onBackground = NeuTextPrimary,
    surface = NeuSurface,
    onSurface = NeuTextPrimary,
    surfaceVariant = NeuSurfaceVariant,
    onSurfaceVariant = NeuTextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NeuColorScheme,
        typography = AppTypography,
        content = content
    )
}
