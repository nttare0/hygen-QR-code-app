package com.example.ui.neumorphic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuDarkShadow
import com.example.ui.theme.NeuDarkShadowPressed
import com.example.ui.theme.NeuLightShadow
import com.example.ui.theme.NeuSurface

/**
 * Draws a classic raised tactile neumorphic surface with dual directional soft light/shadow.
 */
fun Modifier.neuRaised(
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    elevation: Dp = 6.dp,
    lightShadow: Color = NeuLightShadow,
    darkShadow: Color = NeuDarkShadow,
    backgroundColor: Color = NeuSurface
): Modifier = this
    .drawBehind {
        val cornerRadius = CornerRadius(cornerRadiusDp.toPx(), cornerRadiusDp.toPx())
        val offsetPx = elevation.toPx()

        // 1. Soft Light Highlight (Top-Left)
        drawRoundRect(
            color = lightShadow.copy(alpha = 0.9f),
            topLeft = Offset(-offsetPx * 0.8f, -offsetPx * 0.8f),
            size = size,
            cornerRadius = cornerRadius
        )

        // 2. Soft Ambient Dark Shadow (Bottom-Right)
        drawRoundRect(
            color = darkShadow.copy(alpha = 0.7f),
            topLeft = Offset(offsetPx, offsetPx),
            size = size,
            cornerRadius = cornerRadius
        )
    }
    .clip(shape)
    .background(backgroundColor)

/**
 * Draws a debossed (sunken/inset) tactile neumorphic surface for wells, scanner frames, and pressed buttons.
 */
fun Modifier.neuDebossed(
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    depth: Dp = 4.dp,
    lightShadow: Color = NeuLightShadow,
    darkShadow: Color = NeuDarkShadowPressed,
    backgroundColor: Color = NeuSurface
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .drawWithContent {
        drawContent()

        val cornerRadius = CornerRadius(cornerRadiusDp.toPx(), cornerRadiusDp.toPx())
        val depthPx = depth.toPx()

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = cornerRadius.x,
                    radiusY = cornerRadius.y
                )
            )
        }

        clipPath(path) {
            // Dark Inset shadow from Top-Left
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(darkShadow.copy(alpha = 0.5f), Color.Transparent),
                    start = Offset(0f, 0f),
                    end = Offset(depthPx * 2f, depthPx * 2f)
                ),
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = cornerRadius
            )

            // Light Inset highlight from Bottom-Right
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, lightShadow.copy(alpha = 0.8f)),
                    start = Offset(size.width - depthPx * 2f, size.height - depthPx * 2f),
                    end = Offset(size.width, size.height)
                ),
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = cornerRadius
            )
        }
    }

/**
 * Tactile Neumorphic Button that animates between raised and pressed states on touch.
 */
@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    elevation: Dp = 6.dp,
    backgroundColor: Color = NeuSurface,
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedElevation by animateFloatAsState(
        targetValue = if (isPressed) 1f else elevation.value,
        animationSpec = tween(durationMillis = 100),
        label = "neuElevation"
    )

    val surfaceModifier = if (isPressed) {
        Modifier.neuDebossed(shape, cornerRadiusDp, 3.dp, backgroundColor = backgroundColor)
    } else {
        Modifier.neuRaised(shape, cornerRadiusDp, animatedElevation.dp, backgroundColor = backgroundColor)
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    isPressed = true
                    val up = waitForUpOrCancellation()
                    isPressed = false
                    if (up != null) {
                        onClick()
                    }
                }
            }
            .then(surfaceModifier),
        contentAlignment = Alignment.Center,
        content = content
    )
}
