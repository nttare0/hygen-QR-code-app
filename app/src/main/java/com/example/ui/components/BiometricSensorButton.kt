package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary

@Composable
fun BiometricSensorButton(
    isAuthenticating: Boolean,
    onAuthenticateClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Touch to Pay"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "biometricPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAuthenticating) 1.15f else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(110.dp)
        ) {
            // Outer debossed well (soft circular depression)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .neuDebossed(
                        shape = CircleShape,
                        cornerRadiusDp = 50.dp,
                        depth = 4.dp,
                        backgroundColor = NeuBackground
                    )
            )

            // Pulsing tactile sensor ring
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .scale(if (isAuthenticating) pulseScale else 1f)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isAuthenticating) NeuAccentGreen else NeuAccentBlue.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )

            val haptics = rememberTactileHaptics()

            // Inner Raised tactile sensor button
            NeuButton(
                onClick = {
                    haptics.biometricScanPulse()
                    onAuthenticateClick()
                },
                enableHaptics = false,
                shape = CircleShape,
                cornerRadiusDp = 35.dp,
                elevation = 5.dp,
                modifier = Modifier
                    .size(70.dp)
                    .testTag("biometric_sensor_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Touch Sensor",
                    tint = if (isAuthenticating) NeuAccentGreen else NeuAccentBlue,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isAuthenticating) NeuAccentGreen else NeuTextPrimary
        )

        Text(
            text = if (isAuthenticating) "Verifying fingerprint..." else "Touch ID or Face ID",
            fontSize = 11.sp,
            color = NeuTextSecondary
        )
    }
}
