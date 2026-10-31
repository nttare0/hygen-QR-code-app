package com.example.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuInsetDeep
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.DmSansFontFamily
import com.example.ui.theme.NeuAccent
import com.example.ui.theme.NeuAccentLight
import com.example.ui.theme.NeuAccentSecondary
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuSurfaceVariant
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.PlusJakartaFontFamily

@Composable
fun CameraScannerView(
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    onQrDetected: (merchantName: String, category: String, amount: Double, rawPayload: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isFlashlightOn by remember { mutableStateOf(false) }
    var cameraControl: androidx.camera.core.CameraControl? by remember { mutableStateOf(null) }
    val haptics = rememberTactileHaptics()

    val infiniteTransition = rememberInfiniteTransition(label = "scannerMotion")

    // Smooth laser scanline animation inside the optical chamber
    val scanOffsetFraction by infiniteTransition.animateFloat(
        initialValue = 0.14f,
        targetValue = 0.86f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanY"
    )

    // 3s ambient floating animation for the tactile emblem in the well
    val floatOffsetY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientFloat"
    )

    // Pulse animation for status indicator & reticle glow
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Level 1 (Extruded): Outer 32dp Pure White Neumorphic Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(32.dp),
                    cornerRadiusDp = 32.dp,
                    elevation = 10.dp,
                    backgroundColor = NeuSurface
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Level 2 (Inset Bezel): Sculpted Outer Bezel Ring framing the Screen Well
                Box(
                    modifier = Modifier
                        .size(296.dp)
                        .neuDebossed(
                            shape = RoundedCornerShape(30.dp),
                            cornerRadiusDp = 30.dp,
                            depth = 5.dp,
                            backgroundColor = NeuBackground
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Level 3 (Extruded Lip): Subtle pure white raised collar around the optical chamber
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .neuRaised(
                                shape = RoundedCornerShape(26.dp),
                                cornerRadiusDp = 26.dp,
                                elevation = 4.dp,
                                backgroundColor = NeuSurface
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Level 4 (Deep Inset Screen Well): Carved 3D Optical Viewfinder Chamber
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .neuInsetDeep(
                                    shape = RoundedCornerShape(22.dp),
                                    cornerRadiusDp = 22.dp,
                                    depth = 11.dp,
                                    backgroundColor = NeuBackground
                                )
                                .clip(RoundedCornerShape(22.dp))
                                .testTag("qr_scanner_viewfinder"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hasCameraPermission) {
                                // Live CameraX Preview inside the carved Screen Well
                                AndroidView(
                                    factory = { ctx ->
                                        val previewView = PreviewView(ctx)
                                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                        cameraProviderFuture.addListener({
                                            try {
                                                val cameraProvider = cameraProviderFuture.get()
                                                val preview = Preview.Builder().build().also {
                                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                                }
                                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                                cameraProvider.unbindAll()
                                                val camera = cameraProvider.bindToLifecycle(
                                                    lifecycleOwner,
                                                    cameraSelector,
                                                    preview
                                                )
                                                cameraControl = camera.cameraControl
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }, ContextCompat.getMainExecutor(ctx))
                                        previewView
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Ambient Tactile Scanner Well Surface (Monochromatic Cool Clay)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    NeuSurface,
                                                    NeuSurfaceVariant.copy(alpha = 0.65f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        // Nested depth inside well: Inset socket -> Extruded floating emblem
                                        Box(
                                            modifier = Modifier
                                                .graphicsLayer { translationY = floatOffsetY }
                                                .size(84.dp)
                                                .neuDebossed(
                                                    shape = CircleShape,
                                                    cornerRadiusDp = 42.dp,
                                                    depth = 5.dp,
                                                    backgroundColor = NeuBackground
                                                )
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .neuRaised(
                                                        shape = CircleShape,
                                                        cornerRadiusDp = 34.dp,
                                                        elevation = 5.dp,
                                                        backgroundColor = NeuSurface
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.QrCodeScanner,
                                                    contentDescription = "Scanner Ready",
                                                    tint = NeuAccent,
                                                    modifier = Modifier.size(34.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Text(
                                            text = "Ready to Scan",
                                            fontFamily = PlusJakartaFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = NeuTextPrimary
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Align QR code inside optical well",
                                            fontFamily = DmSansFontFamily,
                                            fontSize = 12.sp,
                                            color = NeuTextSecondary
                                        )
                                    }
                                }
                            }

                            // Borderless Sculpted Corner Reticles & Luminous Scan Beam
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp)
                            ) {
                                val strokeWidth = 3.5.dp.toPx()
                                val cornerLen = 28.dp.toPx()
                                val arcDiameter = 32.dp.toPx()
                                val reticleBrush = Brush.linearGradient(
                                    colors = listOf(
                                        NeuAccent.copy(alpha = 0.88f * pulseAlpha.coerceAtLeast(0.75f)),
                                        NeuAccentLight.copy(alpha = 0.72f)
                                    )
                                )
                                val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

                                // Top-Left Corner Bracket
                                drawArc(
                                    brush = reticleBrush,
                                    startAngle = 180f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(0f, 0f),
                                    size = Size(arcDiameter, arcDiameter),
                                    style = stroke
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(arcDiameter / 2f, 0f),
                                    end = Offset(cornerLen + 8.dp.toPx(), 0f),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(0f, arcDiameter / 2f),
                                    end = Offset(0f, cornerLen + 8.dp.toPx()),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )

                                // Top-Right Corner Bracket
                                drawArc(
                                    brush = reticleBrush,
                                    startAngle = 270f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(size.width - arcDiameter, 0f),
                                    size = Size(arcDiameter, arcDiameter),
                                    style = stroke
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(size.width - cornerLen - 8.dp.toPx(), 0f),
                                    end = Offset(size.width - arcDiameter / 2f, 0f),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(size.width, arcDiameter / 2f),
                                    end = Offset(size.width, cornerLen + 8.dp.toPx()),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )

                                // Bottom-Left Corner Bracket
                                drawArc(
                                    brush = reticleBrush,
                                    startAngle = 90f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(0f, size.height - arcDiameter),
                                    size = Size(arcDiameter, arcDiameter),
                                    style = stroke
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(0f, size.height - cornerLen - 8.dp.toPx()),
                                    end = Offset(0f, size.height - arcDiameter / 2f),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(arcDiameter / 2f, size.height),
                                    end = Offset(cornerLen + 8.dp.toPx(), size.height),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )

                                // Bottom-Right Corner Bracket
                                drawArc(
                                    brush = reticleBrush,
                                    startAngle = 0f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    topLeft = Offset(size.width - arcDiameter, size.height - arcDiameter),
                                    size = Size(arcDiameter, arcDiameter),
                                    style = stroke
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(size.width - cornerLen - 8.dp.toPx(), size.height),
                                    end = Offset(size.width - arcDiameter / 2f, size.height),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    brush = reticleBrush,
                                    start = Offset(size.width, size.height - cornerLen - 8.dp.toPx()),
                                    end = Offset(size.width, size.height - arcDiameter / 2f),
                                    strokeWidth = strokeWidth,
                                    cap = StrokeCap.Round
                                )

                                // Soft Luminous Scanning Beam
                                val beamY = size.height * scanOffsetFraction
                                val glowHeight = 18.dp.toPx()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            NeuAccent.copy(alpha = 0.18f),
                                            Color.Transparent
                                        ),
                                        startY = beamY - glowHeight,
                                        endY = beamY + glowHeight
                                    ),
                                    topLeft = Offset(10.dp.toPx(), beamY - glowHeight),
                                    size = Size(size.width - 20.dp.toPx(), glowHeight * 2f)
                                )
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            NeuAccentLight,
                                            NeuAccent,
                                            NeuAccentLight,
                                            Color.Transparent
                                        )
                                    ),
                                    start = Offset(8.dp.toPx(), beamY),
                                    end = Offset(size.width - 8.dp.toPx(), beamY),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Debossed Status Pill with Teal Pulse Indicator
                Box(
                    modifier = Modifier
                        .neuDebossed(
                            shape = CircleShape,
                            cornerRadiusDp = 18.dp,
                            depth = 3.dp,
                            backgroundColor = NeuBackground
                        )
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = NeuAccentSecondary.copy(alpha = pulseAlpha),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasCameraPermission) "Optical Scanner Active" else "Optical Well Ready",
                            fontFamily = PlusJakartaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = NeuTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sculpted Control Wells (Extruded 48dp+ Buttons Nested inside Inset Sockets)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Torch Control Socket (Extruded 48dp button nested in 58dp inset well)
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .neuDebossed(
                                shape = CircleShape,
                                cornerRadiusDp = 29.dp,
                                depth = 4.dp,
                                backgroundColor = NeuBackground
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        NeuButton(
                            onClick = {
                                val nextTorch = !isFlashlightOn
                                isFlashlightOn = nextTorch
                                haptics.toggleSwitch(nextTorch)
                                cameraControl?.enableTorch(nextTorch)
                            },
                            enableHaptics = false,
                            shape = CircleShape,
                            cornerRadiusDp = 24.dp,
                            elevation = 4.dp,
                            backgroundColor = if (isFlashlightOn) NeuAccent.copy(alpha = 0.14f) else NeuSurface,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("flashlight_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isFlashlightOn) Icons.Default.FlashOn else Icons.Default.FlashlightOff,
                                contentDescription = "Toggle Torch",
                                tint = if (isFlashlightOn) NeuAccent else NeuTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Primary Interactive Scan Action (Extruded 48dp hero button nested in 58dp inset track)
                    Box(
                        modifier = Modifier
                            .height(58.dp)
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .neuDebossed(
                                shape = RoundedCornerShape(20.dp),
                                cornerRadiusDp = 20.dp,
                                depth = 4.dp,
                                backgroundColor = NeuBackground
                            )
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NeuButton(
                            onClick = {
                                onQrDetected(
                                    "School Cafeteria",
                                    "Food & Snacks",
                                    15.00,
                                    "QUICKPAY_QR_SCHOOL_CAFETERIA_15.00"
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            cornerRadiusDp = 16.dp,
                            elevation = 5.dp,
                            backgroundColor = NeuSurface,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("primary_scan_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "Scan QR Code",
                                    tint = NeuAccent,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Scan Code",
                                    fontFamily = PlusJakartaFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = NeuTextPrimary
                                )
                            }
                        }
                    }

                    // Camera Permission / Live Lens Socket (Extruded 48dp button nested in 58dp inset well)
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .neuDebossed(
                                shape = CircleShape,
                                cornerRadiusDp = 29.dp,
                                depth = 4.dp,
                                backgroundColor = NeuBackground
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        NeuButton(
                            onClick = onRequestPermission,
                            shape = CircleShape,
                            cornerRadiusDp = 24.dp,
                            elevation = 4.dp,
                            backgroundColor = if (hasCameraPermission) NeuAccentSecondary.copy(alpha = 0.12f) else NeuSurface,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("camera_permission_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Access",
                                tint = if (hasCameraPermission) NeuAccentSecondary else NeuTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
