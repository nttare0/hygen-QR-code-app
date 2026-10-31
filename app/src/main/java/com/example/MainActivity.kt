package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.neumorphic.LocalHapticsEnabled
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuDebossed
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.QuickScannerScreen
import com.example.ui.screens.SecuritySettingsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.viewmodel.PaymentViewModel

enum class NavTab(val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector, val tag: String) {
    SCAN("Scan", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner, "nav_tab_scan"),
    WALLET("Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_tab_wallet"),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History, "nav_tab_history"),
    SECURITY("Security", Icons.Filled.Security, Icons.Outlined.Security, "nav_tab_security")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    viewModel: PaymentViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(NavTab.SCAN) }
    val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()

    // Back handler to navigate back to SCAN home if on another screen
    BackHandler(enabled = currentTab != NavTab.SCAN) {
        currentTab = NavTab.SCAN
    }

    CompositionLocalProvider(LocalHapticsEnabled provides isHapticsEnabled) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(NeuBackground)
            .statusBarsPadding(),
        containerColor = NeuBackground,
        bottomBar = {
            // Tactile Neumorphic Bottom Navigation Bar with clear icons & labels
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .neuRaised(
                            shape = RoundedCornerShape(26.dp),
                            cornerRadiusDp = 26.dp,
                            elevation = 8.dp,
                            backgroundColor = NeuSurface
                        )
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavTab.values().forEach { tab ->
                            val isSelected = currentTab == tab
                            TactileNavItem(
                                tab = tab,
                                isSelected = isSelected,
                                onClick = { currentTab = tab },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag(tab.tag)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    NavTab.SCAN -> QuickScannerScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { currentTab = it }
                    )
                    NavTab.WALLET -> WalletScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentTab = NavTab.SCAN },
                        onNavigateToScan = { currentTab = NavTab.SCAN }
                    )
                    NavTab.HISTORY -> HistoryScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentTab = NavTab.SCAN },
                        onNavigateToScan = { currentTab = NavTab.SCAN }
                    )
                    NavTab.SECURITY -> SecuritySettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentTab = NavTab.SCAN }
                    )
                }
            }
        }
    }
    }
}

@Composable
fun TactileNavItem(
    tab: NavTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptics = rememberTactileHaptics()

    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (!isSelected) {
                        haptics.tapClick()
                    } else {
                        haptics.pressDown()
                    }
                    onClick()
                }
            )
            .then(
                if (isSelected) {
                    Modifier
                        .neuDebossed(
                            shape = RoundedCornerShape(18.dp),
                            cornerRadiusDp = 18.dp,
                            depth = 4.dp,
                            backgroundColor = NeuBackground
                        )
                        .padding(vertical = 4.dp)
                } else {
                    Modifier.padding(vertical = 4.dp)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            // Icon with soft highlight
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(NeuAccentBlue.copy(alpha = 0.14f), CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                    contentDescription = tab.title,
                    tint = if (isSelected) NeuAccentBlue else NeuTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Text Label always clearly visible for child accessibility
            Text(
                text = tab.title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isSelected) NeuAccentBlue else NeuTextSecondary
            )
        }
    }
}
