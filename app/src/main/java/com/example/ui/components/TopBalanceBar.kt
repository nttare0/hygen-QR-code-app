package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.neumorphic.NeuButton
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.ManropeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopBalanceBar(
    balance: Double,
    currency: String,
    bankName: String,
    cardLast4: String = "",
    isDefaultCard: Boolean = true,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(balance)

    val linkedSubtitle = buildString {
        append("BANK LINKED: $bankName")
        if (cardLast4.isNotBlank()) {
            append(" • •••• $cardLast4")
            if (isDefaultCard) append(" (Default)")
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Teal #18A999 strictly for verified bank status
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Active",
                        tint = NeuAccentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = linkedSubtitle,
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = NeuTextMuted
                        ),
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Total Available Balance",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = NeuTextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quick card / wallet action button (Indigo #5B5CE2 action on Pure White #FFFFFF)
            NeuButton(
                onClick = onCardClick,
                shape = CircleShape,
                cornerRadiusDp = 24.dp,
                elevation = 5.dp,
                backgroundColor = NeuSurface,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("top_wallet_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "Wallet Cards",
                    tint = NeuAccentBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Large balance in Manrope Bold with Deep Navy #17243B & neutral slate currency prefix
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$currency ",
                style = TextStyle(
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = NeuTextSecondary
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = formattedBalance,
                style = TextStyle(
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    fontFeatureSettings = "tnum",
                    color = NeuTextPrimary
                ),
                modifier = Modifier.testTag("main_balance_text")
            )
        }
    }
}
