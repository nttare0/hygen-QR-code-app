package com.example.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuRaised
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentOrange
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary

data class QuickDemoQr(
    val title: String,
    val merchantName: String,
    val category: String,
    val amount: Double,
    val iconName: String
)

val SampleDemoQrs = listOf(
    QuickDemoQr("Cafeteria Lunch", "School Fresh Cafe", "Food & Meals", 15.00, "🍔"),
    QuickDemoQr("Bookstore", "Knowledge Books & Stationery", "Books & Education", 32.50, "📚"),
    QuickDemoQr("Toy Store", "Wonderland Toys & Games", "Kids Toys", 45.00, "🎮"),
    QuickDemoQr("Ice Cream Treat", "Gelato Joy Corner", "Snacks & Sweets", 8.50, "🍦"),
    QuickDemoQr("Art Supplies", "Creative Craft Studio", "Art & Hobbies", 24.00, "🎨")
)

@Composable
fun CameraScannerView(
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    onQrDetected: (merchantName: String, category: String, amount: Double, rawPayload: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isFlashlightOn by remember { mutableStateOf(false) }
    var cameraControl: androidx.camera.core.CameraControl? by remember { mutableStateOf(null) }

    // Animated laser scanline
    val transition = rememberInfiniteTransition(label = "scanline")
    val scanOffsetFraction by transition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanY"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Viewfinder Card (Debossed well with raised corners)
        Box(
            modifier = Modifier
                .size(260.dp)
                .neuDebossed(
                    shape = RoundedCornerShape(28.dp),
                    cornerRadiusDp = 28.dp,
                    depth = 6.dp
                )
                .clip(RoundedCornerShape(28.dp))
                .testTag("qr_scanner_viewfinder"),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission) {
                // Live CameraX Preview
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
                // Friendly fallback view with camera request
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "QR Scanner Icon",
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Camera Ready to Scan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeuTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Point at QR code or tap demo below",
                        fontSize = 11.sp,
                        color = NeuTextSecondary
                    )
                }
            }

            // Viewfinder Corner Brackets & Laser
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .border(
                        width = 2.5.dp,
                        color = NeuAccentOrange.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(18.dp)
                    )
            )

            // Animated Laser Line
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(3.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = (scanOffsetFraction * 180).dp)
                    .background(
                        color = NeuAccentOrange,
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scanner Control Bar (Flashlight, Demo Picker, Status)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuButton(
                onClick = {
                    isFlashlightOn = !isFlashlightOn
                    cameraControl?.enableTorch(isFlashlightOn)
                },
                shape = CircleShape,
                cornerRadiusDp = 20.dp,
                elevation = 4.dp,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("flashlight_toggle_button")
            ) {
                Icon(
                    imageVector = if (isFlashlightOn) Icons.Default.FlashOn else Icons.Default.FlashlightOff,
                    contentDescription = "Toggle Torch",
                    tint = if (isFlashlightOn) NeuAccentOrange else NeuTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Scanning Code...",
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = NeuTextSecondary
            )

            NeuButton(
                onClick = onRequestPermission,
                shape = CircleShape,
                cornerRadiusDp = 20.dp,
                elevation = 4.dp,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("camera_permission_button")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Permissions",
                    tint = NeuAccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Child-Friendly Instant Demo QR Bar
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Instant 1-Tap QR Demo Codes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NeuTextPrimary
                )
                Text(
                    text = "Tap to test",
                    fontSize = 11.sp,
                    color = NeuAccentBlue
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SampleDemoQrs) { item ->
                    DemoQrChip(
                        demo = item,
                        onClick = {
                            onQrDetected(
                                item.merchantName,
                                item.category,
                                item.amount,
                                "QUICKPAY_QR_${item.merchantName.uppercase().replace(" ", "_")}_${item.amount}"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DemoQrChip(
    demo: QuickDemoQr,
    onClick: () -> Unit
) {
    NeuButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        cornerRadiusDp = 14.dp,
        elevation = 3.dp,
        modifier = Modifier.testTag("demo_qr_${demo.title.replace(" ", "_").lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = demo.iconName, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = demo.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = NeuTextPrimary
                )
                Text(
                    text = "SAR ${"%.2f".format(demo.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NeuAccentBlue
                )
            }
        }
    }
}
