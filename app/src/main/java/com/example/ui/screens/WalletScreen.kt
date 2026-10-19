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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ThreeDCardView
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
    val isNfcEnabled by viewModel.isNfcEnabled.collectAsState()

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
                modifier = Modifier
                    .height(40.dp)
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
                modifier = Modifier
                    .height(40.dp)
                    .testTag("wallet_quick_scan_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan QR",
                        tint = NeuAccentOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pay QR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeuAccentOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Tactile Wallet",
                fontFamily = PlayfairFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = NeuTextPrimary
            )
            Text(
                text = "Manage your linked bank cards and allowances",
                fontSize = 13.sp,
                color = NeuTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3D Card Stack
        ThreeDCardView(
            accounts = accounts,
            selectedAccountId = selectedAccountId,
            onSelectAccount = { viewModel.selectAccount(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Child Pocket Money Deposit Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .neuRaised(
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = 6.dp
                )
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = "Piggy Bank",
                        tint = NeuAccentGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Pocket Money Deposit",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NeuTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Add simulated allowance to test transactions instantly.",
                    fontSize = 12.sp,
                    color = NeuTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NeuButton(
                        onClick = { viewModel.addPocketMoney(25.0) },
                        shape = RoundedCornerShape(12.dp),
                        cornerRadiusDp = 12.dp,
                        elevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("add_money_25")
                    ) {
                        Text(
                            text = "+ SAR 25",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeuAccentGreen
                        )
                    }

                    NeuButton(
                        onClick = { viewModel.addPocketMoney(50.0) },
                        shape = RoundedCornerShape(12.dp),
                        cornerRadiusDp = 12.dp,
                        elevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("add_money_50")
                    ) {
                        Text(
                            text = "+ SAR 50",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeuAccentGreen
                        )
                    }

                    NeuButton(
                        onClick = { viewModel.addPocketMoney(100.0) },
                        shape = RoundedCornerShape(12.dp),
                        cornerRadiusDp = 12.dp,
                        elevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("add_money_100")
                    ) {
                        Text(
                            text = "+ SAR 100",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeuAccentGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Configuration: NFC Tools Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .neuRaised(
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = 5.dp
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
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NFC Contactless Tools",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = "Enable tap-to-pay terminals",
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
                    modifier = Modifier.testTag("nfc_tools_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(110.dp))
    }
}
