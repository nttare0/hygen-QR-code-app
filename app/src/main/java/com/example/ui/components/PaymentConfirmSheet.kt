package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun PaymentConfirmSheet(
    merchantName: String,
    merchantCategory: String,
    amount: Double,
    currency: String,
    selectedAccount: BankAccount,
    safetyResult: MerchantSafetyResult?,
    isSafetyLoading: Boolean,
    isAuthenticating: Boolean,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaised(
                        shape = RoundedCornerShape(28.dp),
                        cornerRadiusDp = 28.dp,
                        elevation = 10.dp,
                        backgroundColor = NeuBackground
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
                                tint = NeuAccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirm Payment",
                                fontFamily = PlayfairFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = NeuTextPrimary
                            )
                        }

                        NeuButton(
                            onClick = onDismiss,
                            shape = CircleShape,
                            cornerRadiusDp = 16.dp,
                            elevation = 3.dp,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("close_payment_sheet_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = NeuTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Merchant Details Card (Debossed well)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuDebossed(
                                shape = RoundedCornerShape(18.dp),
                                cornerRadiusDp = 18.dp,
                                depth = 4.dp
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = merchantName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NeuTextPrimary
                            )
                            Text(
                                text = merchantCategory,
                                fontSize = 12.sp,
                                color = NeuTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$currency ${"%,.2f".format(amount)}",
                                fontFamily = PlayfairFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = NeuAccentBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gemini 3.1 Flash Lite Low-Latency Safety Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuRaised(
                                shape = RoundedCornerShape(14.dp),
                                cornerRadiusDp = 14.dp,
                                elevation = 3.dp
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
                                            tint = if (safetyResult.isSafe) NeuAccentGreen else NeuAccentOrange,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = safetyResult.safetyBadge,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (safetyResult.isSafe) NeuAccentGreen else NeuAccentOrange
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Fast AI",
                                            tint = NeuAccentBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${safetyResult.latencyMs}ms",
                                            fontFamily = FiraCodeFontFamily,
                                            fontSize = 10.sp,
                                            color = NeuTextMuted
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = safetyResult.summary,
                                    fontSize = 11.sp,
                                    color = NeuTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linked Payment Account indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Card",
                                tint = NeuAccentBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${selectedAccount.cardType} (•••• ${selectedAccount.last4})",
                                fontSize = 12.sp,
                                fontFamily = FiraCodeFontFamily,
                                color = NeuTextPrimary
                            )
                        }

                        Text(
                            text = "Balance: SAR ${"%,.2f".format(selectedAccount.balance)}",
                            fontSize = 11.sp,
                            color = NeuTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Biometric Sensor Button (One-touch authorization)
                    BiometricSensorButton(
                        isAuthenticating = isAuthenticating,
                        onAuthenticateClick = onConfirmPayment,
                        label = "Hold Finger to Pay",
                        modifier = Modifier.testTag("confirm_biometric_button")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
