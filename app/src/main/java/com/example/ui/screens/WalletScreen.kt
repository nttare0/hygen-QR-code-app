package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CardManagementPanel
import com.example.ui.components.LinkBankCardDialog
import com.example.ui.components.ThreeDCardView
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.viewmodel.PaymentViewModel

@Composable
fun WalletScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.bankAccounts.collectAsState()
    val selectedAccountId by viewModel.selectedAccountId.collectAsState()
    val defaultAccountId by viewModel.defaultAccountId.collectAsState()
    val isNfcEnabled by viewModel.isNfcEnabled.collectAsState()
    val haptics = rememberTactileHaptics()
    var showLinkCardDialog by remember { mutableStateOf(false) }

    val currentAccount = accounts.find { it.id == selectedAccountId } ?: accounts.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Header with Back and Scan buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                    .testTag("wallet_back_button")
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

            NeuButton(
                onClick = onNavigateToScan,
                shape = RoundedCornerShape(14.dp),
                cornerRadiusDp = 14.dp,
                elevation = 3.dp,
                backgroundColor = NeuSurface,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("wallet_quick_scan_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan QR",
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pay QR",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeuAccentBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Tactile Wallet",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = NeuTextPrimary
            )
            Text(
                text = "Link bank cards, choose your default card, and set limits",
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                color = NeuTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3D Card Stack + Link Bank Card CTA
        ThreeDCardView(
            accounts = accounts,
            selectedAccountId = selectedAccountId,
            defaultAccountId = defaultAccountId,
            onSelectAccount = { viewModel.selectAccount(it) },
            onSetDefaultAccount = { viewModel.setDefaultAccount(it) },
            onLinkNewCardClick = { showLinkCardDialog = true }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Selected Card Controls, Default Payment Selector & Transaction Limits
        CardManagementPanel(
            account = currentAccount,
            isDefaultCard = currentAccount.id == defaultAccountId,
            canRemoveCard = accounts.size > 1,
            onSetDefault = { viewModel.setDefaultAccount(currentAccount.id) },
            onToggleFreeze = { frozen -> viewModel.toggleCardFreeze(currentAccount.id, frozen) },
            onUpdateLimit = { newLimit -> viewModel.updateCardTransactionLimit(currentAccount.id, newLimit) },
            onRemoveCard = { viewModel.removeBankCard(currentAccount.id) }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Instant Pocket Money Deposit Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .neuRaised(
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = 6.dp,
                    backgroundColor = NeuSurface
                )
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Piggy Bank",
                        tint = NeuTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Pocket Money Deposit",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NeuTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Add funds to ${currentAccount.cardType} (•••• ${currentAccount.last4}) instantly.",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = NeuTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(25, 50, 100).forEach { depositAmount ->
                        NeuButton(
                            onClick = {
                                haptics.transactionSuccess()
                                viewModel.addPocketMoney(depositAmount.toDouble())
                            },
                            enableHaptics = false,
                            shape = RoundedCornerShape(12.dp),
                            cornerRadiusDp = 12.dp,
                            elevation = 3.dp,
                            backgroundColor = NeuBackground,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("add_money_$depositAmount")
                        ) {
                            Text(
                                text = "+ SAR $depositAmount",
                                fontFamily = ManropeFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeuAccentBlue
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NFC Contactless Tools Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .neuRaised(
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = 5.dp,
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
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NFC Contactless Tools",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Enable tap-to-pay terminals",
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
                    modifier = Modifier.testTag("nfc_tools_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }

    if (showLinkCardDialog) {
        LinkBankCardDialog(
            onDismiss = { showLinkCardDialog = false },
            onConfirmLink = { bankName, cardType, last4, initialBalance, txLimit, expiry, makeDefault ->
                viewModel.linkBankCard(
                    bankName = bankName,
                    cardType = cardType,
                    last4 = last4,
                    initialBalance = initialBalance,
                    perTransactionLimit = txLimit,
                    expiryDate = expiry,
                    makeDefault = makeDefault
                )
                showLinkCardDialog = false
            }
        )
    }
}
