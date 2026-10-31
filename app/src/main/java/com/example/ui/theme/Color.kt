package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Final Recommended Palette:
// - Background: Soft blue #F3F7FC
// - Cards: Pure white #FFFFFF
// - Primary color (actions): Indigo #5B5CE2
// - Success / Verified states: Teal #18A999
// - Text: Deep navy #17243B
// - Rule: Purple/Indigo for actions, Teal for successful/verified states, Neutrals for everything else.

val NeuBackground = Color(0xFFF3F7FC)       // Soft blue page background
val NeuSurface = Color(0xFFFFFFFF)          // Pure white raised cards & buttons
val NeuSurfaceVariant = Color(0xFFE9EFF8)   // Soft neutral recessed well / inset surface

// Balanced Soft UI Shadows calibrated for #FFFFFF cards over #F3F7FC soft blue background
val NeuLightShadow = Color(1f, 1f, 1f, 0.90f)
val NeuLightShadowHover = Color(1f, 1f, 1f, 0.96f)
val NeuDarkShadow = Color(180 / 255f, 196 / 255f, 218 / 255f, 0.44f)
val NeuDarkShadowHover = Color(170 / 255f, 188 / 255f, 212 / 255f, 0.54f)
val NeuDarkShadowDeep = Color(166 / 255f, 184 / 255f, 210 / 255f, 0.62f)

// Strict Semantic Accent Palette
val NeuAccent = Color(0xFF5B5CE2)           // Indigo #5B5CE2 — strictly for interactive actions
val NeuAccentLight = Color(0xFF7C7DF0)      // Soft indigo highlight for gradients & scan beam
val NeuAccentSecondary = Color(0xFF18A999)  // Teal #18A999 — strictly for successful or verified states

// Neutral Icon / Metadata Tokens (replaces coral/orange to keep UI calm and not overly colorful)
val NeuNeutralSlate = Color(0xFF52627A)     // Neutral deep slate for non-action icons & amounts
val NeuAccentOrange = NeuNeutralSlate       // Mapped to neutral slate per color restraint rule

// Typography Colors (Deep Navy #17243B hierarchy)
val NeuTextPrimary = Color(0xFF17243B)      // Deep navy primary text
val NeuTextSecondary = Color(0xFF52627A)    // Calm slate secondary text
val NeuTextMuted = Color(0xFF7B8BA3)        // Muted cool neutral metadata
val NeuTextWhite = Color(0xFFFFFFFF)

// Compatibility Aliases
val NeuAccentBlue = NeuAccent               // Indigo #5B5CE2
val NeuAccentBlueLight = NeuAccentLight
val NeuAccentGreen = NeuAccentSecondary     // Teal #18A999
val NeuDarkShadowPressed = NeuDarkShadowDeep

// Restrained Card Gradients (Indigo primary card + Deep Navy / Cool Slate neutrals — no orange/coral)
val CardGradientBlueStart = Color(0xFF5B5CE2)   // Primary Account: Indigo #5B5CE2
val CardGradientBlueEnd = Color(0xFF4344B8)
val CardGradientPurpleStart = Color(0xFF1E2D4A) // Secondary Account: Deep Navy neutral
val CardGradientPurpleEnd = Color(0xFF17243B)
val CardGradientCoralStart = Color(0xFF3E4E68)  // Tertiary Account: Cool Slate neutral
val CardGradientCoralEnd = Color(0xFF28364D)
