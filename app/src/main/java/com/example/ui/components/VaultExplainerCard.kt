package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary

@Composable
fun VaultExplainerCard(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }
    val haptics = rememberTactileHaptics()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .neuRaised(
                shape = RoundedCornerShape(20.dp),
                cornerRadiusDp = 20.dp,
                elevation = 5.dp,
                backgroundColor = NeuSurface
            )
            .padding(18.dp)
            .testTag("vault_explainer_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptics.tapClick()
                        expanded = !expanded
                    }
                    .testTag("vault_explainer_toggle"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(NeuAccentGreen.copy(alpha = 0.14f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Vault Active",
                            tint = NeuAccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "What is the Security Vault?",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NeuTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(NeuAccentGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = NeuAccentGreen
                                )
                            }
                        }
                        Text(
                            text = "Your on-device hardware shield for linked cards & QR payments",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse Vault guide" else "Expand Vault guide",
                    tint = NeuAccentBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuDebossed(
                                shape = RoundedCornerShape(16.dp),
                                cornerRadiusDp = 16.dp,
                                depth = 3.dp,
                                backgroundColor = NeuBackground
                            )
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            VaultStepRow(
                                icon = Icons.Default.Shield,
                                title = "1. Zero-Exposure Card Tokenization",
                                description = "Your real 16-digit bank card numbers stay encrypted on your phone. Stores only receive a one-time payment code."
                            )
                            VaultStepRow(
                                icon = Icons.Default.Fingerprint,
                                title = "2. Biometric Payment Gate",
                                description = "Every QR scan requires Touch ID or Face ID before money can leave your default or selected card."
                            )
                            VaultStepRow(
                                icon = Icons.Default.Lock,
                                title = "3. Dual Transaction Guardrails",
                                description = "Automatically blocks any charge above your global Vault cap or individual card limit—or if a card is frozen."
                            )
                            VaultStepRow(
                                icon = Icons.Default.Bolt,
                                title = "4. Instant AI Merchant Check",
                                description = "Gemini 3.1 Flash Lite inspects each QR merchant in under 300ms to warn you before confirming."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultStepRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeuTextSecondary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(17.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = NeuTextPrimary
            )
            Text(
                text = description,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = NeuTextSecondary
            )
        }
    }
}
