package com.example.ui.neumorphic

import android.graphics.BlurMaskFilter
import android.graphics.Paint as AndroidPaint
import android.graphics.RectF
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeuAccent
import com.example.ui.theme.NeuDarkShadow
import com.example.ui.theme.NeuDarkShadowDeep
import com.example.ui.theme.NeuLightShadow
import com.example.ui.theme.NeuSurface

/**
 * Extruded (Convex / Raised) Soft UI Surface:
 * Renders dual RGBA blurred shadows using native BlurMaskFilter so elements appear
 * smoothly molded out of the #E0E5EC cool clay continuous surface without hard edges.
 *
 * Standard token: 9dp offset, 16dp blur — rgb(163,177,198,0.65) & rgba(255,255,255,0.6)
 */
fun Modifier.neuRaised(
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    elevation: Dp = 9.dp,
    blurRadius: Dp = (elevation.value * 1.8f).dp,
    lightShadow: Color = NeuLightShadow,
    darkShadow: Color = NeuDarkShadow,
    backgroundColor: Color = NeuSurface
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadiusDp.toPx()
        val offsetPx = elevation.toPx()
        val blurPx = blurRadius.toPx().coerceAtLeast(1f)

        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas

            // Top-Left Light Highlight (rgba(255, 255, 255, 0.6))
            val lightPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                style = AndroidPaint.Style.FILL
                color = lightShadow.toArgb()
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
            val lightRect = RectF(
                -offsetPx,
                -offsetPx,
                size.width - offsetPx,
                size.height - offsetPx
            )
            nativeCanvas.drawRoundRect(lightRect, radiusPx, radiusPx, lightPaint)

            // Bottom-Right Cool Dark Shadow (rgb(163, 177, 198, 0.65))
            val darkPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                style = AndroidPaint.Style.FILL
                color = darkShadow.toArgb()
                maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
            }
            val darkRect = RectF(
                offsetPx,
                offsetPx,
                size.width + offsetPx,
                size.height + offsetPx
            )
            nativeCanvas.drawRoundRect(darkRect, radiusPx, radiusPx, darkPaint)
        }
    }
    .clip(shape)
    .background(backgroundColor)

/**
 * Standard Inset (Concave / Pressed / Shallow Well):
 * inset 6px 6px 10px rgb(163,177,198,0.65), inset -6px -6px 10px rgba(255,255,255,0.6)
 * Uses clipped blurred inner stroke rings for authentic pillowed concave depth.
 */
fun Modifier.neuDebossed(
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    depth: Dp = 6.dp,
    lightShadow: Color = NeuLightShadow,
    darkShadow: Color = NeuDarkShadow,
    backgroundColor: Color = NeuSurface
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .drawWithContent {
        drawContent()

        val radiusPx = cornerRadiusDp.toPx()
        val depthPx = depth.toPx()
        val blurPx = (depthPx * 1.6f).coerceAtLeast(1f)
        val strokePx = depthPx * 2.2f

        val clipShapePath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = radiusPx,
                    radiusY = radiusPx
                )
            )
        }

        clipPath(clipShapePath) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas

                // Top-Left Carved Shadow Rim
                val darkPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    style = AndroidPaint.Style.STROKE
                    strokeWidth = strokePx
                    color = darkShadow.toArgb()
                    maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
                }
                val darkInsetRect = RectF(
                    -depthPx * 0.5f,
                    -depthPx * 0.5f,
                    size.width + depthPx * 1.5f,
                    size.height + depthPx * 1.5f
                )
                nativeCanvas.drawRoundRect(darkInsetRect, radiusPx, radiusPx, darkPaint)

                // Bottom-Right Soft Light Catch Rim
                val lightPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    style = AndroidPaint.Style.STROKE
                    strokeWidth = strokePx
                    color = lightShadow.toArgb()
                    maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
                }
                val lightInsetRect = RectF(
                    -depthPx * 1.5f,
                    -depthPx * 1.5f,
                    size.width + depthPx * 0.5f,
                    size.height + depthPx * 0.5f
                )
                nativeCanvas.drawRoundRect(lightInsetRect, radiusPx, radiusPx, lightPaint)
            }
        }
    }

/**
 * Inset Deep: Specifically designed for the Hero Screen Well and deep carved optical chambers.
 * inset 10px 10px 20px rgb(163,177,198,0.85), inset -10px -10px 20px rgba(255,255,255,0.7)
 * Combines blurred native inner shadows with subtle directional ambient falloff.
 */
fun Modifier.neuInsetDeep(
    shape: Shape = RoundedCornerShape(26.dp),
    cornerRadiusDp: Dp = 26.dp,
    depth: Dp = 10.dp,
    lightShadow: Color = NeuLightShadow,
    darkShadow: Color = NeuDarkShadowDeep,
    backgroundColor: Color = NeuSurface
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .drawWithContent {
        drawContent()

        val radiusPx = cornerRadiusDp.toPx()
        val cornerRadius = CornerRadius(radiusPx, radiusPx)
        val depthPx = depth.toPx()
        val blurPx = (depthPx * 1.9f).coerceAtLeast(1f)
        val strokePx = depthPx * 2.6f

        val clipShapePath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = radiusPx,
                    radiusY = radiusPx
                )
            )
        }

        clipPath(clipShapePath) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas

                // Deep Carved Top-Left Shadow
                val darkPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    style = AndroidPaint.Style.STROKE
                    strokeWidth = strokePx
                    color = darkShadow.toArgb()
                    maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
                }
                val darkInsetRect = RectF(
                    -depthPx * 0.4f,
                    -depthPx * 0.4f,
                    size.width + depthPx * 1.8f,
                    size.height + depthPx * 1.8f
                )
                nativeCanvas.drawRoundRect(darkInsetRect, radiusPx, radiusPx, darkPaint)

                // Deep Carved Bottom-Right Highlight
                val lightPaint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
                    style = AndroidPaint.Style.STROKE
                    strokeWidth = strokePx
                    color = lightShadow.copy(alpha = 0.8f).toArgb()
                    maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
                }
                val lightInsetRect = RectF(
                    -depthPx * 1.8f,
                    -depthPx * 1.8f,
                    size.width + depthPx * 0.4f,
                    size.height + depthPx * 0.4f
                )
                nativeCanvas.drawRoundRect(lightInsetRect, radiusPx, radiusPx, lightPaint)
            }

            // Soft top-left ambient depth gradient for smooth bowl feel
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        darkShadow.copy(alpha = 0.28f),
                        Color.Transparent,
                        lightShadow.copy(alpha = 0.22f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                ),
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = cornerRadius
            )
        }
    }

/**
 * Tactile Neumorphic Button with 300ms smooth physics, physical press depression,
 * subtle haptic press/release vibration, minimum 48dp accessible touch target,
 * and 2dp #6C63FF focus ring support.
 */
@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    cornerRadiusDp: Dp = 16.dp,
    elevation: Dp = 6.dp,
    backgroundColor: Color = NeuSurface,
    enableHaptics: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val haptics = rememberTactileHaptics()

    val animatedElevation by animateFloatAsState(
        targetValue = if (isPressed) 1f else elevation.value,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "neuElevation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "neuScale"
    )

    val animatedTranslateY by animateFloatAsState(
        targetValue = if (isPressed) 2f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "neuTranslateY"
    )

    val surfaceModifier = if (isPressed) {
        Modifier.neuDebossed(shape, cornerRadiusDp, 5.dp, backgroundColor = backgroundColor)
    } else {
        Modifier.neuRaised(shape, cornerRadiusDp, animatedElevation.dp, backgroundColor = backgroundColor)
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .semantics {
                role = Role.Button
                onClick {
                    if (enableHaptics) {
                        haptics.tapClick()
                    }
                    onClick()
                    true
                }
            }
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                translationY = animatedTranslateY
            }
            .pointerInput(onClick, enableHaptics) {
                awaitEachGesture {
                    awaitFirstDown()
                    isPressed = true
                    if (enableHaptics) {
                        haptics.pressDown()
                    }
                    val up = waitForUpOrCancellation()
                    isPressed = false
                    if (up != null) {
                        if (enableHaptics) {
                            haptics.tapClick()
                        }
                        onClick()
                    }
                }
            }
            .then(surfaceModifier)
            .drawWithContent {
                drawContent()
                if (isFocused) {
                    val strokePx = 2.dp.toPx()
                    val rPx = cornerRadiusDp.toPx()
                    drawRoundRect(
                        color = NeuAccent,
                        topLeft = Offset(strokePx / 2f, strokePx / 2f),
                        size = androidx.compose.ui.geometry.Size(
                            size.width - strokePx,
                            size.height - strokePx
                        ),
                        cornerRadius = CornerRadius(rPx, rPx),
                        style = Stroke(width = strokePx)
                    )
                }
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}
