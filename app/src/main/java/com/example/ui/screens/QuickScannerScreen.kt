package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.NavTab
import com.example.ui.components.CameraScannerView
import com.example.ui.components.PaymentConfirmSheet
import com.example.ui.components.TopBalanceBar
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.DmSansFontFamily
import com.example.ui.theme.NeuAccent
import com.example.ui.theme.NeuAccentSecondary
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.NeuTextWhite
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.PlusJakartaFontFamily
import com.example.ui.viewmodel.PaymentViewModel
import kotlin.math.min

@Composable
fun QuickScannerScreen(
    viewModel: PaymentViewModel,
    onNavigateToTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accounts by viewModel.bankAccounts.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val defaultAccountId by viewModel.defaultAccountId.collectAsState()
    val vaultSpendingLimit by viewModel.childSpendingLimit.collectAsState()
    val activeScan by viewModel.activeScan.collectAsState()
    val safetyResult by viewModel.safetyResult.collectAsState()
    val isSafetyLoading by viewModel.isSafetyLoading.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val paymentSuccess by viewModel.paymentSuccess.collectAsState()
    val paymentBlockReason by viewModel.paymentBlockReason.collectAsState()
    val haptics = rememberTactileHaptics()

    // Trigger celebratory harmonic haptic pattern whenever a transaction completes successfully
    LaunchedEffect(paymentSuccess?.transactionId) {
        if (paymentSuccess != null) {
            haptics.transactionSuccess()
        }
    }

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeuBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top balance bar with readable card details & Default indicator
            TopBalanceBar(
                balance = currentAccount.balance,
                currency = currentAccount.currency,
                bankName = currentAccount.bankName,
                cardLast4 = currentAccount.last4,
                isDefaultCard = currentAccount.id == defaultAccountId,
                onCardClick = { onNavigateToTab(NavTab.WALLET) }
            )

            // Accessible 48dp Tactile Quick Navigation Pills with 16dp Soft UI radii
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wallet shortcut pill
                NeuButton(
                    onClick = { onNavigateToTab(NavTab.WALLET) },
                    shape = RoundedCornerShape(16.dp),
                    cornerRadiusDp = 16.dp,
                    elevation = 5.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_jump_wallet")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = NeuTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cards",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuTextPrimary
                        )
                    }
                }

                // History shortcut pill
                NeuButton(
                    onClick = { onNavigateToTab(NavTab.HISTORY) },
                    shape = RoundedCornerShape(16.dp),
                    cornerRadiusDp = 16.dp,
                    elevation = 5.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_jump_history")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = NeuTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "History",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuTextPrimary
                        )
                    }
                }

                // Security Vault shortcut pill
                NeuButton(
                    onClick = { onNavigateToTab(NavTab.SECURITY) },
                    shape = RoundedCornerShape(16.dp),
                    cornerRadiusDp = 16.dp,
                    elevation = 5.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_jump_security")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Vault",
                            tint = NeuTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Vault",
                            fontFamily = PlusJakartaFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hero Neumorphic Deep Inset Screen Well (Clean, Minimal & Clutter-Free)
            CameraScannerView(
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = { launcher.launch(Manifest.permission.CAMERA) },
                onQrDetected = { name, category, amount, payload ->
                    viewModel.onQrScanned(name, category, amount, payload)
                }
            )

            Spacer(modifier = Modifier.height(108.dp))
        }

        // Active Payment Confirmation Sheet (When QR is scanned/tapped)
        activeScan?.let { scan ->
            val liveGuardrailIssue = paymentBlockReason
                ?: viewModel.evaluatePaymentGuardrail(currentAccount, scan.amount)
            val effectiveCap = min(currentAccount.perTransactionLimit, vaultSpendingLimit)

            PaymentConfirmSheet(
                merchantName = scan.merchantName,
                merchantCategory = scan.category,
                amount = scan.amount,
                currency = "SAR",
                selectedAccount = currentAccount,
                allAccounts = accounts,
                defaultAccountId = defaultAccountId,
                effectiveLimit = effectiveCap,
                guardrailIssue = liveGuardrailIssue,
                safetyResult = safetyResult,
                isSafetyLoading = isSafetyLoading,
                isAuthenticating = isAuthenticating,
                onSelectAccount = { viewModel.selectAccount(it) },
                onOpenWalletControls = {
                    viewModel.dismissPaymentSheet()
                    onNavigateToTab(NavTab.WALLET)
                },
                onConfirmPayment = { viewModel.executeBiometricPayment() },
                onDismiss = { viewModel.dismissPaymentSheet() }
            )
        }

        // Payment Success Modal (32dp Neumorphic Soft UI Container)
        paymentSuccess?.let { success ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .neuRaised(
                            shape = RoundedCornerShape(32.dp),
                            cornerRadiusDp = 32.dp,
                            elevation = 12.dp,
                            backgroundColor = NeuSurface
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
                                .size(76.dp)
                                .neuDebossed(
                                    shape = CircleShape,
                                    cornerRadiusDp = 38.dp,
                                    depth = 4.dp,
                                    backgroundColor = NeuBackground
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(NeuAccentSecondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = NeuTextWhite,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Payment Complete!",
                            fontFamily = PlusJakartaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = NeuTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Paid to ${success.merchantName}",
                            fontFamily = DmSansFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = NeuTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${success.currency} ${"%,.2f".format(success.amount)}",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = NeuTextPrimary
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        NeuButton(
                            onClick = { viewModel.dismissSuccessMessage() },
                            shape = RoundedCornerShape(16.dp),
                            cornerRadiusDp = 16.dp,
                            elevation = 5.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("done_success_button")
                        ) {
                            Text(
                                text = "Done",
                                fontFamily = PlusJakartaFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NeuAccent
                            )
                        }
                    }
                }
            }
        }
    }
}
