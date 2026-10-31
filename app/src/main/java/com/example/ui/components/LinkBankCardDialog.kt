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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
fun LinkBankCardDialog(
    onDismiss: () -> Unit,
    onConfirmLink: (
        bankName: String,
        cardType: String,
        last4: String,
        initialBalance: Double,
        perTransactionLimit: Double,
        expiryDate: String,
        makeDefault: Boolean
    ) -> Unit
) {
    val haptics = rememberTactileHaptics()
    var bankName by remember { mutableStateOf("Al Rajhi Digital") }
    var cardType by remember { mutableStateOf("Visa Debit") }
    var last4 by remember { mutableStateOf("6428") }
    var initialBalanceText by remember { mutableStateOf("5000") }
    var limitText by remember { mutableStateOf("1000") }
    var expiryDate by remember { mutableStateOf("06/30") }
    var makeDefault by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cardTypes = listOf("Visa Debit", "Mastercard", "Credit", "Savings")
    val quickBanks = listOf("Al Rajhi Digital", "SNB Saudi", "Riyad Bank")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp)
                    .neuRaised(
                        shape = RoundedCornerShape(28.dp),
                        cornerRadiusDp = 28.dp,
                        elevation = 10.dp,
                        backgroundColor = NeuSurface
                    )
                    .padding(22.dp)
                    .testTag("link_card_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddCard,
                                contentDescription = "Link Bank Card",
                                tint = NeuAccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Link Bank Card",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
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
                                .testTag("close_link_card_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close dialog",
                                tint = NeuTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = "Tokenized securely inside your device Vault",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = NeuTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Bank Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickBanks.forEach { preset ->
                            val isSelected = bankName == preset
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) NeuAccentBlue.copy(alpha = 0.12f) else NeuBackground
                                    )
                                    .clickable {
                                        haptics.tapClick()
                                        bankName = preset
                                    }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset,
                                    fontFamily = InterFontFamily,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) NeuAccentBlue else NeuTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bank Name Input
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = {
                            bankName = it
                            errorMessage = null
                        },
                        label = { Text("Bank Name", fontFamily = InterFontFamily, fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeuAccentBlue,
                            unfocusedBorderColor = NeuBackground,
                            focusedContainerColor = NeuBackground,
                            unfocusedContainerColor = NeuBackground,
                            focusedTextColor = NeuTextPrimary,
                            unfocusedTextColor = NeuTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("link_card_bank_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Type Selector
                    Text(
                        text = "Card Network / Type",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = NeuTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cardTypes.forEach { typeOption ->
                            val selected = cardType == typeOption
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) NeuAccentBlue else NeuBackground)
                                    .clickable {
                                        haptics.tapClick()
                                        cardType = typeOption
                                    }
                                    .padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = typeOption,
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = if (selected) Color.White else NeuTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Last 4 Digits + Expiry Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = last4,
                            onValueChange = { input ->
                                last4 = input.filter { it.isDigit() }.take(4)
                                errorMessage = null
                            },
                            label = { Text("Last 4 Digits", fontFamily = InterFontFamily, fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeuAccentBlue,
                                unfocusedBorderColor = NeuBackground,
                                focusedContainerColor = NeuBackground,
                                unfocusedContainerColor = NeuBackground,
                                focusedTextColor = NeuTextPrimary,
                                unfocusedTextColor = NeuTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("link_card_last4_input")
                        )

                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it.take(5) },
                            label = { Text("Expiry (MM/YY)", fontFamily = InterFontFamily, fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeuAccentBlue,
                                unfocusedBorderColor = NeuBackground,
                                focusedContainerColor = NeuBackground,
                                unfocusedContainerColor = NeuBackground,
                                focusedTextColor = NeuTextPrimary,
                                unfocusedTextColor = NeuTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("link_card_expiry_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Starting Balance & Per-Transaction Limit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = initialBalanceText,
                            onValueChange = { input ->
                                initialBalanceText = input.filter { it.isDigit() || it == '.' }.take(8)
                                errorMessage = null
                            },
                            label = { Text("Balance (SAR)", fontFamily = InterFontFamily, fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeuAccentBlue,
                                unfocusedBorderColor = NeuBackground,
                                focusedContainerColor = NeuBackground,
                                unfocusedContainerColor = NeuBackground,
                                focusedTextColor = NeuTextPrimary,
                                unfocusedTextColor = NeuTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("link_card_balance_input")
                        )

                        OutlinedTextField(
                            value = limitText,
                            onValueChange = { input ->
                                limitText = input.filter { it.isDigit() || it == '.' }.take(6)
                                errorMessage = null
                            },
                            label = { Text("Tx Limit (SAR)", fontFamily = InterFontFamily, fontSize = 12.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeuAccentBlue,
                                unfocusedBorderColor = NeuBackground,
                                focusedContainerColor = NeuBackground,
                                unfocusedContainerColor = NeuBackground,
                                focusedTextColor = NeuTextPrimary,
                                unfocusedTextColor = NeuTextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("link_card_limit_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Set as Default Payment Card Toggle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neuDebossed(
                                shape = RoundedCornerShape(16.dp),
                                cornerRadiusDp = 16.dp,
                                depth = 3.dp,
                                backgroundColor = NeuBackground
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Set as Default Payment Card",
                                        fontFamily = ManropeFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NeuTextPrimary
                                    )
                                    if (makeDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Default Verified",
                                            tint = NeuAccentGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Use automatically for QR & contactless scans",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = NeuTextSecondary
                                )
                            }

                            Switch(
                                checked = makeDefault,
                                onCheckedChange = {
                                    haptics.toggleSwitch(it)
                                    makeDefault = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeuAccentBlue,
                                    checkedTrackColor = NeuAccentBlue.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.testTag("link_card_default_switch")
                            )
                        }
                    }

                    errorMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = NeuTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action Button (Indigo #5B5CE2)
                    NeuButton(
                        onClick = {
                            if (bankName.isBlank()) {
                                errorMessage = "Please enter a bank name."
                                return@NeuButton
                            }
                            if (last4.length < 4) {
                                errorMessage = "Please enter the last 4 digits of your card."
                                return@NeuButton
                            }
                            val parsedBalance = initialBalanceText.toDoubleOrNull() ?: 0.0
                            val parsedLimit = (limitText.toDoubleOrNull() ?: 1000.0).coerceAtLeast(25.0)
                            haptics.transactionSuccess()
                            onConfirmLink(
                                bankName,
                                cardType,
                                last4,
                                parsedBalance,
                                parsedLimit,
                                expiryDate,
                                makeDefault
                            )
                        },
                        enableHaptics = false,
                        shape = RoundedCornerShape(16.dp),
                        cornerRadiusDp = 16.dp,
                        elevation = 5.dp,
                        backgroundColor = NeuAccentBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_link_card_button")
                    ) {
                        Text(
                            text = "Link Bank Card",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
