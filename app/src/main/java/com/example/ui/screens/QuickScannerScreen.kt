package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.NavTab
import com.example.ui.components.CameraScannerView
import com.example.ui.components.PaymentConfirmSheet
import com.example.ui.components.TopBalanceBar
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuRaised
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuAccentOrange
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.NeuTextWhite
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.viewmodel.PaymentViewModel

@Composable
fun QuickScannerScreen(
    viewModel: PaymentViewModel,
    onNavigateToTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accounts by viewModel.bankAccounts.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val activeScan by viewModel.activeScan.collectAsState()
    val safetyResult by viewModel.safetyResult.collectAsState()
    val isSafetyLoading by viewModel.isSafetyLoading.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val paymentSuccess by viewModel.paymentSuccess.collectAsState()

    val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.first()

    // Camera permission handling
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Manual custom QR dialog state
    var showCustomQrDialog by remember { mutableStateOf(false) }
    var customStoreName by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeuBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top balance bar with Playfair Display balance
            TopBalanceBar(
                balance = currentAccount.balance,
                currency = currentAccount.currency,
                bankName = currentAccount.bankName,
                onCardClick = { onNavigateToTab(NavTab.WALLET) }
            )

            // Quick navigation jump pills right under the balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Wallet shortcut pill
                NeuButton(
                    onClick = { onNavigateToTab(NavTab.WALLET) },
                    shape = RoundedCornerShape(12.dp),
                    cornerRadiusDp = 12.dp,
                    elevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("quick_jump_wallet")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = NeuAccentBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "My Cards",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuTextPrimary
                        )
                    }
                }

                // History shortcut pill
                NeuButton(
                    onClick = { onNavigateToTab(NavTab.HISTORY) },
                    shape = RoundedCornerShape(12.dp),
                    cornerRadiusDp = 12.dp,
                    elevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("quick_jump_history")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = NeuAccentOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "History",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuTextPrimary
                        )
                    }
                }

                // Security shortcut pill
                NeuButton(
                    onClick = { onNavigateToTab(NavTab.SECURITY) },
                    shape = RoundedCornerShape(12.dp),
                    cornerRadiusDp = 12.dp,
                    elevation = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("quick_jump_security")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = NeuAccentGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Security",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Interactive Camera Scanner View
            CameraScannerView(
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = { launcher.launch(Manifest.permission.CAMERA) },
                onQrDetected = { name, category, amount, payload ->
                    viewModel.onQrScanned(name, category, amount, payload)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Custom QR or Manual Pay Button
            NeuButton(
                onClick = { showCustomQrDialog = true },
                shape = RoundedCornerShape(16.dp),
                cornerRadiusDp = 16.dp,
                elevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp)
                    .testTag("custom_qr_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Custom QR",
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Custom Store / Amount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeuTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick App Navigation Section (Child-friendly cards)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Quick Shortcuts",
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeuTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Wallet Card
                QuickNavCard(
                    title = "Tactile Wallet & Cards",
                    subtitle = "Manage bank cards and add pocket money allowance",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconTint = NeuAccentBlue,
                    onClick = { onNavigateToTab(NavTab.WALLET) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // History Card
                QuickNavCard(
                    title = "Transaction & Scan History",
                    subtitle = "View past receipts with AI breakdown",
                    icon = Icons.Default.History,
                    iconTint = NeuAccentOrange,
                    onClick = { onNavigateToTab(NavTab.HISTORY) }
                )
            }

            Spacer(modifier = Modifier.height(110.dp))
        }

        // Active Payment Confirmation Sheet (When QR is scanned/tapped)
        activeScan?.let { scan ->
            PaymentConfirmSheet(
                merchantName = scan.merchantName,
                merchantCategory = scan.category,
                amount = scan.amount,
                currency = "SAR",
                selectedAccount = currentAccount,
                safetyResult = safetyResult,
                isSafetyLoading = isSafetyLoading,
                isAuthenticating = isAuthenticating,
                onConfirmPayment = { viewModel.executeBiometricPayment() },
                onDismiss = { viewModel.dismissPaymentSheet() }
            )
        }

        // Success Popup Banner / Dialog
        paymentSuccess?.let { success ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .neuRaised(
                            shape = RoundedCornerShape(24.dp),
                            cornerRadiusDp = 24.dp,
                            elevation = 12.dp,
                            backgroundColor = NeuBackground
                        )
                        .padding(28.dp)
                        .testTag("payment_success_modal"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .background(NeuAccentGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = NeuTextWhite,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Payment Complete!",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = NeuTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Paid to ${success.merchantName}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = NeuTextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${success.currency} ${"%,.2f".format(success.amount)}",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = NeuAccentBlue
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        NeuButton(
                            onClick = { viewModel.dismissSuccessMessage() },
                            shape = RoundedCornerShape(14.dp),
                            cornerRadiusDp = 14.dp,
                            elevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("done_success_button")
                        ) {
                            Text(
                                text = "Done",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NeuAccentGreen
                            )
                        }
                    }
                }
            }
        }

        // Custom QR Code Dialog
        if (showCustomQrDialog) {
            AlertDialog(
                onDismissRequest = { showCustomQrDialog = false },
                title = {
                    Text(
                        text = "Quick Custom QR",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Enter store name and amount to generate an instant QR pay sheet.",
                            fontSize = 13.sp,
                            color = NeuTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = customStoreName,
                            onValueChange = { customStoreName = it },
                            label = { Text("Store Name") },
                            placeholder = { Text("e.g. Science Fair") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_store_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customAmount,
                            onValueChange = { customAmount = it },
                            label = { Text("Amount (SAR)") },
                            placeholder = { Text("e.g. 25.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_amount_input")
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val amt = customAmount.toDoubleOrNull() ?: 10.0
                            val store = if (customStoreName.isNotBlank()) customStoreName else "Custom Store"
                            showCustomQrDialog = false
                            viewModel.onQrScanned(
                                store,
                                "General Purchase",
                                amt,
                                "CUSTOM_QR_${store.uppercase()}_$amt"
                            )
                        },
                        modifier = Modifier.testTag("submit_custom_qr_button")
                    ) {
                        Text("Scan & Pay", fontWeight = FontWeight.Bold, color = NeuAccentBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomQrDialog = false }) {
                        Text("Cancel", color = NeuTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    NeuButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        cornerRadiusDp = 16.dp,
        elevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(iconTint.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeuTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = NeuTextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = NeuTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
