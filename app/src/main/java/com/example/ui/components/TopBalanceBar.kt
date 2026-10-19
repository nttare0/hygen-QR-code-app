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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.neumorphic.NeuButton
import com.example.ui.neumorphic.neuRaised
import com.example.ui.theme.FiraCodeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuSurface
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.PlayfairFontFamily
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopBalanceBar(
    balance: Double,
    currency: String,
    bankName: String,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedBalance = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }.format(balance)

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
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Active",
                        tint = NeuAccentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BANK LINKED: $bankName",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = FiraCodeFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = NeuTextMuted
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Total Available Balance",
                    style = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = NeuTextSecondary
                    )
                )
            }

            // Quick card / wallet button
            NeuButton(
                onClick = onCardClick,
                shape = CircleShape,
                cornerRadiusDp = 22.dp,
                elevation = 4.dp,
                modifier = Modifier
                    .size(44.dp)
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

        // Large balance in Playfair Display Serif as specified in prompt
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$currency ",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = NeuAccentBlue
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = formattedBalance,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = NeuTextPrimary
                ),
                modifier = Modifier.testTag("main_balance_text")
            )
        }
    }
}
