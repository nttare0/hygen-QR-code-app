package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionRecord
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.FiraCodeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuAccentOrange
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuSurfaceVariant
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.viewmodel.PaymentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.allTransactions.collectAsState()
    var selectedTab by remember { mutableStateOf("Today") } // "Today" or "All"
    var selectedTransactionForDetails by remember { mutableStateOf<TransactionRecord?>(null) }

    val now = System.currentTimeMillis()
    val startOfToday = now - (now % (24 * 60 * 60 * 1000))

    val filteredList = if (selectedTab == "Today") {
        transactions.filter { it.timestamp >= startOfToday }
    } else {
        transactions
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuBackground)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Header with Back and Scan Shortcuts
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
                    .testTag("history_back_button")
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
                    .testTag("history_quick_scan_button")
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeuAccentBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scanning History",
                fontFamily = PlayfairFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = NeuTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "All QR payments and deposits stored securely on device",
                fontSize = 12.sp,
                color = NeuTextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Segmented Tabs ("Today" and "All") — Indigo #5B5CE2 for active selection
            Row(
                modifier = Modifier
                    .neuDebossed(
                        shape = RoundedCornerShape(24.dp),
                        cornerRadiusDp = 24.dp,
                        depth = 3.dp,
                        backgroundColor = NeuSurfaceVariant
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val isToday = selectedTab == "Today"
                NeuButton(
                    onClick = { selectedTab = "Today" },
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = if (isToday) 4.dp else 1.dp,
                    backgroundColor = if (isToday) NeuAccentBlue else Color.Transparent,
                    modifier = Modifier
                        .width(110.dp)
                        .height(38.dp)
                        .testTag("history_tab_today")
                ) {
                    Text(
                        text = "Today",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isToday) Color.White else NeuTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                val isAll = selectedTab == "All"
                NeuButton(
                    onClick = { selectedTab = "All" },
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = if (isAll) 4.dp else 1.dp,
                    backgroundColor = if (isAll) NeuAccentBlue else Color.Transparent,
                    modifier = Modifier
                        .width(110.dp)
                        .height(38.dp)
                        .testTag("history_tab_all")
                ) {
                    Text(
                        text = "All",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isAll) Color.White else NeuTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = "Empty History",
                        tint = NeuTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Transactions $selectedTab",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NeuTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan any QR code on the home screen to make your first payment!",
                        fontSize = 12.sp,
                        color = NeuTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NeuButton(
                        onClick = onNavigateToScan,
                        shape = RoundedCornerShape(14.dp),
                        cornerRadiusDp = 14.dp,
                        elevation = 4.dp,
                        backgroundColor = NeuSurface,
                        modifier = Modifier
                            .height(44.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Scan Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeuAccentBlue,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        onClick = { selectedTransactionForDetails = tx },
                        onDelete = { viewModel.deleteTransaction(tx.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }
        }

        // Details / Receipt Dialog
        selectedTransactionForDetails?.let { tx ->
            AlertDialog(
                onDismissRequest = { selectedTransactionForDetails = null },
                containerColor = NeuSurface,
                title = {
                    Text(
                        text = "Smart Receipt Details",
                        fontFamily = PlayfairFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = NeuTextPrimary
                    )
                },
                text = {
                    Column {
                        Text(
                            text = tx.merchantName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NeuTextPrimary
                        )
                        Text(
                            text = tx.merchantCategory,
                            fontSize = 12.sp,
                            color = NeuTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Amount: ${tx.currency} ${"%,.2f".format(tx.amount)}",
                            fontFamily = PlayfairFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (tx.isExpense) NeuAccentOrange else NeuAccentGreen
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Security Verification: ${tx.safetyStatus}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = NeuAccentGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tx.safetySummary,
                            fontSize = 12.sp,
                            color = NeuTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Payment Method: ${tx.bankAccountName} (•••• ${tx.paymentCardLast4})",
                            fontFamily = FiraCodeFontFamily,
                            fontSize = 11.sp,
                            color = NeuTextMuted
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedTransactionForDetails = null }) {
                        Text("Close", fontWeight = FontWeight.Bold, color = NeuAccentBlue)
                    }
                }
            )
        }
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(transaction.timestamp))
    val haptics = rememberTactileHaptics()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neuRaised(
                shape = RoundedCornerShape(18.dp),
                cornerRadiusDp = 18.dp,
                elevation = 4.dp
            )
            .clickable {
                haptics.tapClick()
                onClick()
            }
            .padding(14.dp)
            .testTag("tx_item_${transaction.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon badge (Neutral slate for expenses, Teal #18A999 for money-in deposits)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (transaction.isExpense) NeuBackground else NeuAccentGreen.copy(alpha = 0.14f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = "Transaction Type",
                        tint = if (transaction.isExpense) NeuTextSecondary else NeuAccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.merchantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeuTextPrimary
                    )
                    Text(
                        text = "$formattedDate • ${transaction.merchantCategory}",
                        fontSize = 11.sp,
                        color = NeuTextSecondary
                    )
                }
            }

            // Amount and Action
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (transaction.isExpense) "-" else "+"} ${transaction.currency} ${"%,.2f".format(transaction.amount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (transaction.isExpense) NeuTextPrimary else NeuAccentGreen
                    )
                    Text(
                        text = transaction.safetyStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeuAccentGreen
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                NeuButton(
                    onClick = onDelete,
                    shape = CircleShape,
                    cornerRadiusDp = 16.dp,
                    elevation = 2.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_tx_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete record",
                        tint = NeuTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
