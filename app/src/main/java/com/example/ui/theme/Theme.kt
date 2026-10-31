package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuickPayLightColorScheme = lightColorScheme(
    primary = NeuAccent,                  // Indigo #5B5CE2 (actions)
    onPrimary = Color.White,
    primaryContainer = NeuSurfaceVariant, // Neutral soft blue-grey container
    onPrimaryContainer = NeuTextPrimary,
    secondary = NeuAccentSecondary,       // Teal #18A999 (success / verified)
    onSecondary = Color.White,
    tertiary = NeuNeutralSlate,           // Neutral slate
    background = NeuBackground,           // Soft blue #F3F7FC
    onBackground = NeuTextPrimary,        // Deep navy #17243B
    surface = NeuSurface,                 // Pure white #FFFFFF cards
    onSurface = NeuTextPrimary,           // Deep navy #17243B
    surfaceVariant = NeuSurfaceVariant,
    onSurfaceVariant = NeuTextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuickPayLightColorScheme,
        typography = AppTypography,
        content = content
    )
}
