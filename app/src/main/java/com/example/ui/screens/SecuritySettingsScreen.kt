package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuRaised
import com.example.ui.theme.FiraCodeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuAccentOrange
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.viewmodel.PaymentViewModel

@Composable
fun SecuritySettingsScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNfcEnabled by viewModel.isNfcEnabled.collectAsState()
    val isBiometricsRequired by viewModel.isBiometricsRequired.collectAsState()
    val spendingLimit by viewModel.childSpendingLimit.collectAsState()

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
                modifier = Modifier
                    .height(40.dp)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeuAccentBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Security Configuration",
            fontFamily = PlayfairFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = NeuTextPrimary
        )
        Text(
            text = "Bank encryption and biometric authorization",
            fontSize = 12.sp,
            color = NeuTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3D Vault Shield Asset Container
        Box(
            modifier = Modifier
                .size(130.dp)
                .neuRaised(
                    shape = CircleShape,
                    cornerRadiusDp = 65.dp,
                    elevation = 6.dp
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

        Spacer(modifier = Modifier.height(24.dp))

        // Biometric Security Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp
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
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Biometric Lock",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Require Touch ID for every payment",
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isBiometricsRequired,
                    onCheckedChange = { viewModel.toggleBiometrics(it) },
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
                    elevation = 4.dp
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
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NFC Tools",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Contactless reader support",
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isNfcEnabled,
                    onCheckedChange = { viewModel.toggleNfc(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeuAccentBlue,
                        checkedTrackColor = NeuAccentBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("settings_nfc_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Child Spending Limit Protection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .neuRaised(
                    shape = RoundedCornerShape(18.dp),
                    cornerRadiusDp = 18.dp,
                    elevation = 4.dp
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
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Spending Limit",
                        tint = NeuAccentOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Child Spending Limit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Max allowance per transaction",
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Text(
                    text = "SAR ${spendingLimit.toInt()}",
                    fontFamily = FiraCodeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeuAccentOrange
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
                    elevation = 4.dp
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
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeuAccentGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Real-time merchant verification (<300ms)",
                        fontSize = 11.sp,
                        color = NeuTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}
