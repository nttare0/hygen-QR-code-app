package com.example.ui.components

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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BankAccount
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
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary

@Composable
fun CardManagementPanel(
    account: BankAccount,
    isDefaultCard: Boolean,
    canRemoveCard: Boolean,
    onSetDefault: () -> Unit,
    onToggleFreeze: (Boolean) -> Unit,
    onUpdateLimit: (Double) -> Unit,
    onRemoveCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberTactileHaptics()
    var confirmRemove by remember(account.id) { mutableStateOf(false) }
    val presetLimits = listOf(100.0, 500.0, 1500.0, 3000.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .neuRaised(
                shape = RoundedCornerShape(22.dp),
                cornerRadiusDp = 22.dp,
                elevation = 6.dp,
                backgroundColor = NeuSurface
            )
            .padding(18.dp)
            .testTag("card_management_panel")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Section Header with Readable Card Details Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Card Management",
                        tint = NeuTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Card Controls & Limits",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "${account.bankName} • ${account.cardType} •••• ${account.last4} • EXP ${account.expiryDate}",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = NeuTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Default Payment Card Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuDebossed(
                        shape = RoundedCornerShape(16.dp),
                        cornerRadiusDp = 16.dp,
                        depth = 3.dp,
                        backgroundColor = NeuBackground
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
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
                            imageVector = if (isDefaultCard) Icons.Default.CheckCircle else Icons.Default.CreditCard,
                            contentDescription = "Default Card Status",
                            tint = if (isDefaultCard) NeuAccentGreen else NeuTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isDefaultCard) "Default Payment Card" else "Secondary Linked Card",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeuTextPrimary
                            )
                            Text(
                                text = if (isDefaultCard) {
                                    "Automatically selected when scanning QR codes"
                                } else {
                                    "Make this your primary card for instant QR pay"
                                },
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = NeuTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isDefaultCard) {
                        Box(
                            modifier = Modifier
                                .background(NeuAccentGreen.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("default_card_verified_badge")
                        ) {
                            Text(
                                text = "Default",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = NeuAccentGreen
                            )
                        }
                    } else {
                        NeuButton(
                            onClick = onSetDefault,
                            shape = RoundedCornerShape(12.dp),
                            cornerRadiusDp = 12.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuSurface,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("make_default_card_button")
                        ) {
                            Text(
                                text = "Set Default",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = NeuAccentBlue,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Per-Card Transaction Limit Control
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Single-Transaction Limit",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeuTextPrimary
                            )
                            Text(
                                text = "Max allowed per QR scan on this card",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = NeuTextSecondary
                            )
                        }

                        Text(
                            text = "SAR ${"%,.0f".format(account.perTransactionLimit)}",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NeuTextPrimary,
                            modifier = Modifier.testTag("card_limit_value_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeuButton(
                            onClick = { onUpdateLimit((account.perTransactionLimit - 50.0).coerceAtLeast(25.0)) },
                            shape = CircleShape,
                            cornerRadiusDp = 24.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuSurface,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("card_limit_decrease_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease card limit",
                                tint = NeuAccentBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Slider(
                            value = account.perTransactionLimit.toFloat().coerceIn(25f, 5000f),
                            onValueChange = { onUpdateLimit(it.toDouble()) },
                            valueRange = 25f..5000f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeuAccentBlue,
                                activeTrackColor = NeuAccentBlue,
                                inactiveTrackColor = NeuTextMuted.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .testTag("card_limit_slider")
                        )

                        NeuButton(
                            onClick = { onUpdateLimit((account.perTransactionLimit + 50.0).coerceAtMost(5000.0)) },
                            shape = CircleShape,
                            cornerRadiusDp = 24.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuSurface,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("card_limit_increase_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase card limit",
                                tint = NeuAccentBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Preset Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetLimits.forEach { preset ->
                            val isPresetSelected = account.perTransactionLimit.toInt() == preset.toInt()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isPresetSelected) NeuAccentBlue else NeuSurface)
                                    .clickable {
                                        haptics.tapClick()
                                        onUpdateLimit(preset)
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SAR ${preset.toInt()}",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = if (isPresetSelected) Color.White else NeuTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Freeze Card Switch + Optional Unlink Card Action
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
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Freeze Card",
                        tint = NeuTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (account.isFrozen) "Card Frozen (Paused)" else "Freeze Card",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Temporarily block new QR & NFC charges",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }
                }

                Switch(
                    checked = account.isFrozen,
                    onCheckedChange = {
                        haptics.toggleSwitch(it)
                        onToggleFreeze(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeuAccentBlue,
                        checkedTrackColor = NeuAccentBlue.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("freeze_card_switch")
                )
            }

            if (canRemoveCard) {
                Spacer(modifier = Modifier.height(12.dp))

                if (!confirmRemove) {
                    NeuButton(
                        onClick = { confirmRemove = true },
                        shape = RoundedCornerShape(14.dp),
                        cornerRadiusDp = 14.dp,
                        elevation = 3.dp,
                        backgroundColor = NeuBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("unlink_card_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Unlink Card",
                                tint = NeuTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Unlink ${account.cardType} (•••• ${account.last4})",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = NeuTextSecondary
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeuButton(
                            onClick = { confirmRemove = false },
                            shape = RoundedCornerShape(14.dp),
                            cornerRadiusDp = 14.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuBackground,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Keep Card",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeuTextSecondary
                            )
                        }

                        NeuButton(
                            onClick = {
                                confirmRemove = false
                                onRemoveCard()
                            },
                            shape = RoundedCornerShape(14.dp),
                            cornerRadiusDp = 14.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuAccentBlue,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("confirm_unlink_card_button")
                        ) {
                            Text(
                                text = "Confirm Unlink",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
