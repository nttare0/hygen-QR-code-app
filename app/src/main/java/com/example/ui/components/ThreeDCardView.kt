package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Nfc
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BankAccount
import com.example.ui.neumorphic.neuRaised
import com.example.ui.theme.CardGradientBlueEnd
import com.example.ui.theme.CardGradientBlueStart
import com.example.ui.theme.CardGradientCoralEnd
import com.example.ui.theme.CardGradientCoralStart
import com.example.ui.theme.CardGradientPurpleEnd
import com.example.ui.theme.CardGradientPurpleStart
import com.example.ui.theme.FiraCodeFontFamily
import com.example.ui.theme.NeuAccentBlue
import com.example.ui.theme.NeuAccentGreen
import com.example.ui.theme.NeuBackground
import com.example.ui.theme.NeuTextMuted
import com.example.ui.theme.NeuTextPrimary
import com.example.ui.theme.NeuTextSecondary
import com.example.ui.theme.NeuTextWhite
import com.example.ui.theme.PlayfairFontFamily

@Composable
fun ThreeDCardView(
    accounts: List<BankAccount>,
    selectedAccountId: String,
    onSelectAccount: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Hero 3D Card Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .neuRaised(
                    shape = RoundedCornerShape(20.dp),
                    cornerRadiusDp = 20.dp,
                    elevation = 6.dp
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
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = "Instant Contactless Pay",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Tap or Scan Anywhere",
                        fontFamily = PlayfairFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Linked Accounts",
                fontFamily = PlayfairFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = NeuTextPrimary
            )
            Text(
                text = "${accounts.size} Cards",
                fontSize = 12.sp,
                fontFamily = FiraCodeFontFamily,
                color = NeuTextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stacked / selectable tactile cards
        accounts.forEachIndexed { index, account ->
            val isSelected = account.id == selectedAccountId
            SingleCardItem(
                account = account,
                isSelected = isSelected,
                onClick = { onSelectAccount(account.id) },
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .testTag("card_item_${account.id}")
            )
        }
    }
}

@Composable
fun SingleCardItem(
    account: BankAccount,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = when (account.cardGradientType) {
        1 -> Brush.linearGradient(listOf(CardGradientCoralStart, CardGradientCoralEnd))
        2 -> Brush.linearGradient(listOf(CardGradientPurpleStart, CardGradientPurpleEnd))
        else -> Brush.linearGradient(listOf(CardGradientBlueStart, CardGradientBlueEnd))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick)
            .neuRaised(
                shape = RoundedCornerShape(16.dp),
                cornerRadiusDp = 16.dp,
                elevation = if (isSelected) 8.dp else 4.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.cardType,
                        color = NeuTextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = account.bankName,
                        color = NeuTextWhite.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                if (isSelected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Masked card number in Monospace as required in prompt
                Text(
                    text = "•••• •••• •••• ${account.last4}",
                    fontFamily = FiraCodeFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = NeuTextWhite.copy(alpha = 0.9f)
                )

                Text(
                    text = "SAR ${"%,.2f".format(account.balance)}",
                    fontFamily = PlayfairFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeuTextWhite
                )
            }
        }
    }
}
