package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.VaultExplainerCard
import com.example.ui.neumorphic.NeuButton
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
import com.example.ui.viewmodel.PaymentViewModel

@Composable
fun SecuritySettingsScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNfcEnabled by viewModel.isNfcEnabled.collectAsState()
    val isBiometricsRequired by viewModel.isBiometricsRequired.collectAsState()
    val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()
    val spendingLimit by viewModel.childSpendingLimit.collectAsState()
    val haptics = rememberTactileHaptics()
    val vaultPresets = listOf(50.0, 100.0, 250.0, 500.0, 1000.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Header with Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuButton(
                onClick = onNavigateBack,
                shape = RoundedCornerShape(14.dp),
                cornerRadiusDp = 14.dp,
                elevation = 3.dp,
                backgroundColor = NeuSurface,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("security_back_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Scanner",
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scanner",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeuAccentBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Security Vault",
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = NeuTextPrimary
        )
        Text(
            text = "Hardware card tokenization, transaction caps & biometric lock",
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            color = NeuTextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 3D Vault Shield Asset Container
        Box(
            modifier = Modifier
                .size(124.dp)
                .neuRaised(
                    shape = CircleShape,
                    cornerRadiusDp = 62.dp,
                    elevation = 6.dp,
                    backgroundColor = NeuSurface
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tactile_shield_vault_1792437578334),
                contentDescription = "3D Vault Security Asset",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Clear Explanation of What Vault Means & How It Works
        VaultExplainerCard()

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Vault Global Transaction Limit Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp,
                    backgroundColor = NeuSurface
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Vault Spending Cap",
                            tint = NeuTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Vault Transaction Cap",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeuTextPrimary
                            )
                            Text(
                                text = "Global max allowed per QR scan across all cards",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = NeuTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "SAR ${"%,.0f".format(spendingLimit)}",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NeuTextPrimary,
                        modifier = Modifier.testTag("vault_spending_limit_text")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeuButton(
                        onClick = { viewModel.updateVaultSpendingLimit((spendingLimit - 50.0).coerceAtLeast(25.0)) },
                        shape = CircleShape,
                        cornerRadiusDp = 24.dp,
                        elevation = 3.dp,
                        backgroundColor = NeuBackground,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("vault_limit_decrease_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Vault cap",
                            tint = NeuAccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        vaultPresets.forEach { preset ->
                            val selected = spendingLimit.toInt() == preset.toInt()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) NeuAccentBlue else NeuBackground)
                                    .clickable {
                                        haptics.tapClick()
                                        viewModel.updateVaultSpendingLimit(preset)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${preset.toInt()}",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = if (selected) Color.White else NeuTextSecondary
                                )
                            }
                        }
                    }

                    NeuButton(
                        onClick = { viewModel.updateVaultSpendingLimit((spendingLimit + 50.0).coerceAtMost(10000.0)) },
                        shape = CircleShape,
                        cornerRadiusDp = 24.dp,
                        elevation = 3.dp,
                        backgroundColor = NeuBackground,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("vault_limit_increase_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Vault cap",
                            tint = NeuAccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Biometric Security Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp,
                    backgroundColor = NeuSurface
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometrics",
                        tint = NeuTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Biometric Lock",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Require Touch ID for every payment",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isBiometricsRequired,
                    onCheckedChange = {
                        haptics.toggleSwitch(it)
                        viewModel.toggleBiometrics(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeuAccentBlue,
                        checkedTrackColor = NeuAccentBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("biometric_toggle_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // NFC Tools Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp,
                    backgroundColor = NeuSurface
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = "NFC Tools",
                        tint = NeuTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NFC Tools",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Contactless reader support",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isNfcEnabled,
                    onCheckedChange = {
                        haptics.toggleSwitch(it)
                        viewModel.toggleNfc(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeuAccentBlue,
                        checkedTrackColor = NeuAccentBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("settings_nfc_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tactile Haptic Feedback Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp,
                    backgroundColor = NeuSurface
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Tactile Haptics",
                        tint = NeuTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tactile Haptics",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Subtle vibration on buttons & payments",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isHapticsEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.toggleHaptics(enabled)
                        if (enabled) {
                            haptics.toggleSwitch(true)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeuAccentBlue,
                        checkedTrackColor = NeuAccentBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("haptics_toggle_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gemini 3.1 Flash Lite Status Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp,
                    backgroundColor = NeuSurface
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Gemini AI",
                    tint = NeuAccentGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Gemini 3.1 Flash Lite",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(NeuAccentGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LOW LATENCY",
                                fontFamily = ManropeFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeuAccentGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Real-time merchant verification (<300ms)",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = NeuTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}
