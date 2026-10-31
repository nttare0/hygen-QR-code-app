package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BankAccount
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuRaised
import com.example.ui.neumorphic.rememberTactileHaptics
import com.example.ui.theme.CardGradientBlueEnd
import com.example.ui.theme.CardGradientBlueStart
import com.example.ui.theme.CardGradientCoralEnd
import com.example.ui.theme.CardGradientCoralStart
import com.example.ui.theme.CardGradientPurpleEnd
import com.example.ui.theme.CardGradientPurpleStart
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.NeuTextWhite

@Composable
fun ThreeDCardView(
    accounts: List<BankAccount>,
    selectedAccountId: String,
    defaultAccountId: String,
    onSelectAccount: (String) -> Unit,
    onSetDefaultAccount: (String) -> Unit,
    onLinkNewCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Hero 3D Card Banner with enhanced contrast scrim for readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .neuRaised(
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = 6.dp,
                    backgroundColor = NeuSurface
                )
                .clip(RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.tactile_3d_card_1792437578727),
                contentDescription = "3D Floating Tactile Card",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0E1726).copy(alpha = 0.68f),
                                Color(0xFF0E1726).copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = "Instant Contactless Pay",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap or Scan Anywhere",
                        fontFamily = ManropeFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Header Row with Link Card Action Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Linked Accounts",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NeuTextPrimary
                )
                Text(
                    text = "${accounts.size} linked cards • Tap a card to manage",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = NeuTextMuted
                )
            }

            NeuButton(
                onClick = onLinkNewCardClick,
                shape = RoundedCornerShape(14.dp),
                cornerRadiusDp = 14.dp,
                elevation = 4.dp,
                backgroundColor = NeuSurface,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("link_bank_card_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCard,
                        contentDescription = "Link another bank card",
                        tint = NeuAccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Link Card",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeuAccentBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selectable Tactile Cards with Enhanced Legibility
        accounts.forEach { account ->
            val isSelected = account.id == selectedAccountId
            val isDefault = account.id == defaultAccountId
            SingleCardItem(
                account = account,
                isSelected = isSelected,
                isDefault = isDefault,
                onClick = { onSelectAccount(account.id) },
                onSetDefaultClick = { onSetDefaultAccount(account.id) },
                modifier = Modifier
                    .padding(vertical = 7.dp)
                    .testTag("card_item_${account.id}")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Full-width tactile CTA to link another bank card
        NeuButton(
            onClick = onLinkNewCardClick,
            shape = RoundedCornerShape(16.dp),
            cornerRadiusDp = 16.dp,
            elevation = 4.dp,
            backgroundColor = NeuSurface,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("link_another_card_bottom_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddCard,
                    contentDescription = "Link another bank card",
                    tint = NeuAccentBlue,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Link Another Bank Card",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeuAccentBlue
                )
            }
        }
    }
}

@Composable
fun SingleCardItem(
    account: BankAccount,
    isSelected: Boolean,
    isDefault: Boolean,
    onClick: () -> Unit,
    onSetDefaultClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = when {
        account.isFrozen -> Brush.linearGradient(listOf(Color(0xFF4A586E), Color(0xFF334155)))
        isDefault || account.cardGradientType == 0 -> Brush.linearGradient(listOf(CardGradientBlueStart, CardGradientBlueEnd))
        account.cardGradientType == 1 -> Brush.linearGradient(listOf(CardGradientCoralStart, CardGradientCoralEnd))
        else -> Brush.linearGradient(listOf(CardGradientPurpleStart, CardGradientPurpleEnd))
    }

    val haptics = rememberTactileHaptics()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(136.dp)
            .neuRaised(
                shape = RoundedCornerShape(18.dp),
                cornerRadiusDp = 18.dp,
                elevation = if (isSelected) 8.dp else 4.dp
            )
            .clip(RoundedCornerShape(18.dp))
            .background(gradientBrush)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(18.dp)
                    )
                } else Modifier
            )
            .clickable(
                onClick = {
                    if (!isSelected) {
                        haptics.tapClick()
                    } else {
                        haptics.pressDown()
                    }
                    onClick()
                }
            )
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Card Type + Bank Name + Status Pill (DEFAULT / FROZEN / SELECTED)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = account.cardType,
                        fontFamily = ManropeFontFamily,
                        color = NeuTextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = account.bankName,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = NeuTextWhite.copy(alpha = 0.92f),
                        fontSize = 12.sp
                    )
                }

                when {
                    account.isFrozen -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = "Frozen",
                                tint = NeuTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FROZEN",
                                fontFamily = ManropeFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeuTextPrimary
                            )
                        }
                    }
                    isDefault -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.96f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                                .testTag("card_default_badge_${account.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Default Payment Card",
                                tint = NeuAccentGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DEFAULT",
                                fontFamily = ManropeFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeuTextPrimary
                            )
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = if (isSelected) 0.92f else 0.18f))
                                .clickable {
                                    haptics.tapClick()
                                    onSetDefaultClick()
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("set_default_card_${account.id}")
                        ) {
                            Text(
                                text = "Set Default",
                                fontFamily = ManropeFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeuAccentBlue else Color.White
                            )
                        }
                    }
                }
            }

            // Middle Row: Crisp Masked Card Number + Expiry Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "••••  ••••  ••••  ${account.last4}",
                    style = TextStyle(
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = 1.2.sp,
                        fontFeatureSettings = "tnum",
                        color = NeuTextWhite.copy(alpha = 0.98f)
                    )
                )

                Text(
                    text = "EXP ${account.expiryDate}",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        fontFeatureSettings = "tnum",
                        color = NeuTextWhite.copy(alpha = 0.88f)
                    )
                )
            }

            // Bottom Row: Transaction Limit Indicator + High-Contrast Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Tx Limit: SAR ${"%,.0f".format(account.perTransactionLimit)}",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        fontFeatureSettings = "tnum",
                        color = NeuTextWhite.copy(alpha = 0.90f)
                    )
                )

                Text(
                    text = "${account.currency} ${"%,.2f".format(account.balance)}",
                    style = TextStyle(
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFeatureSettings = "tnum",
                        color = NeuTextWhite
                    )
                )
            }
        }
    }
}
