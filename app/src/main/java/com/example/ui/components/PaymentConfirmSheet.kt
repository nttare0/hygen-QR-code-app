package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.gemini.MerchantSafetyResult
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
import com.example.ui.theme.NeuNeutralSlate
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary

@Composable
fun PaymentConfirmSheet(
    merchantName: String,
    merchantCategory: String,
    amount: Double,
    currency: String,
    selectedAccount: BankAccount,
    allAccounts: List<BankAccount> = listOf(selectedAccount),
    defaultAccountId: String = selectedAccount.id,
    effectiveLimit: Double = selectedAccount.perTransactionLimit,
    guardrailIssue: String? = null,
    safetyResult: MerchantSafetyResult?,
    isSafetyLoading: Boolean,
    isAuthenticating: Boolean,
    onSelectAccount: (String) -> Unit = {},
    onOpenWalletControls: () -> Unit = {},
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    val haptics = rememberTactileHaptics()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pure White #FFFFFF confirmation card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(
                        shape = RoundedCornerShape(28.dp),
                        cornerRadiusDp = 28.dp,
                        elevation = 10.dp,
                        backgroundColor = NeuSurface
                    )
                    .padding(24.dp)
                    .testTag("payment_confirmation_dialog")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Merchant Store",
                                tint = NeuTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirm Payment",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = NeuTextPrimary
                            )
                        }

                        NeuButton(
                            onClick = onDismiss,
                            shape = CircleShape,
                            cornerRadiusDp = 24.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuSurface,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("close_payment_sheet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = NeuTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Merchant Details Card (Soft Blue #F3F7FC Inset Well)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuDebossed(
                                shape = RoundedCornerShape(22.dp),
                                cornerRadiusDp = 22.dp,
                                depth = 6.dp,
                                backgroundColor = NeuBackground
                            )
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = merchantName,
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NeuTextPrimary
                            )
                            Text(
                                text = merchantCategory,
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = NeuTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$currency ${"%,.2f".format(amount)}",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = NeuTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gemini 3.1 Flash Lite Low-Latency Safety Badge (Teal #18A999 for verified state)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuRaised(
                                shape = RoundedCornerShape(14.dp),
                                cornerRadiusDp = 14.dp,
                                elevation = 3.dp,
                                backgroundColor = NeuSurface
                            )
                            .padding(12.dp)
                    ) {
                        if (isSafetyLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = NeuAccentBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini Flash Lite verifying merchant...",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = NeuTextSecondary
                                )
                            }
                        } else if (safetyResult != null) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = "Safe",
                                            tint = if (safetyResult.isSafe) NeuAccentGreen else NeuNeutralSlate,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = safetyResult.safetyBadge,
                                            fontFamily = ManropeFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (safetyResult.isSafe) NeuAccentGreen else NeuNeutralSlate
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Fast AI",
                                            tint = NeuAccentGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${safetyResult.latencyMs}ms",
                                            fontFamily = InterFontFamily,
                                            fontSize = 10.sp,
                                            color = NeuTextMuted
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = safetyResult.summary,
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = NeuTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Linked Payment Card Summary & Quick Switcher
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = "Card",
                                    tint = NeuTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${selectedAccount.cardType} •••• ${selectedAccount.last4}" +
                                        if (selectedAccount.id == defaultAccountId) " (Default)" else "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = InterFontFamily,
                                    color = NeuTextPrimary
                                )
                            }

                            Text(
                                text = "Limit: SAR ${"%,.0f".format(effectiveLimit)}",
                                fontSize = 11.sp,
                                fontFamily = InterFontFamily,
                                color = NeuTextSecondary
                            )
                        }

                        if (allAccounts.size > 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allAccounts.forEach { acc ->
                                    val isCurrent = acc.id == selectedAccount.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isCurrent) NeuAccentBlue else NeuBackground)
                                            .clickable {
                                                haptics.tapClick()
                                                onSelectAccount(acc.id)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${acc.cardType} ••${acc.last4}" +
                                                if (acc.id == defaultAccountId) " ★" else "",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = if (isCurrent) Color.White else NeuTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (guardrailIssue != null) {
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
                                .testTag("payment_guardrail_warning")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Vault Guardrail",
                                        tint = NeuTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Protected by Vault Guardrail",
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NeuTextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = guardrailIssue,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = NeuTextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                NeuButton(
                                    onClick = onOpenWalletControls,
                                    shape = RoundedCornerShape(12.dp),
                                    cornerRadiusDp = 12.dp,
                                    elevation = 3.dp,
                                    backgroundColor = NeuSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("manage_card_limits_from_sheet_button")
                                ) {
                                    Text(
                                        text = "Manage Card & Limits",
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NeuAccentBlue
                                    )
                                }
                            }
                        }
                    } else {
                        // Biometric Sensor Action Button (Indigo #5B5CE2 action, Teal #18A999 when verifying)
                        BiometricSensorButton(
                            isAuthenticating = isAuthenticating,
                            onAuthenticateClick = onConfirmPayment,
                            label = "Hold Finger to Pay",
                            modifier = Modifier.testTag("confirm_biometric_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
