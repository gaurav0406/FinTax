package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalBorder
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

data class CreditCardAnalysis(
    val cardName: String,
    val bankName: String,
    val ranking: String,
    val sentiment: String, // e.g. "89% Positive (High Reward Yield)"
    val isPositiveSentiment: Boolean,
    val rewardsSummary: String,
    val annualFee: String,
    val joiningBonus: String,
    val recentChanges: String,
    val rewardYieldPercent: String
)

val sampleDailyCreditCards = listOf(
    CreditCardAnalysis(
        cardName = "HDFC Regalia Gold",
        bankName = "HDFC Bank",
        ranking = "#1 Recommended Rewards Card",
        sentiment = "92% Positive • High Value Tier",
        isPositiveSentiment = true,
        rewardsSummary = "4X Reward Points on Dining & International Spends. Complimentary Gold Tier Lounge Pass.",
        annualFee = "₹2,500 (Waived on ₹3L annual spend)",
        joiningBonus = "2,500 Reward Points + Club Marriott Membership",
        recentChanges = "Lounge access updated to milestone-based (₹1L/qtr).",
        rewardYieldPercent = "4.5% Effective Yield"
    ),
    CreditCardAnalysis(
        cardName = "SBI Cashback Card",
        bankName = "State Bank of India",
        ranking = "#1 Ranked Flat Cashback Card",
        sentiment = "95% Highly Positive • Unmatched Online Yield",
        isPositiveSentiment = true,
        rewardsSummary = "Flat 5% Cashback on almost all online merchant transactions without merchant restrictions.",
        annualFee = "₹999 (Waived on ₹2L spend)",
        joiningBonus = "1,000 Cashback Points on 1st Swipe",
        recentChanges = "Utility bill cashbacks capped at ₹500/month.",
        rewardYieldPercent = "5.0% Direct Cashback"
    ),
    CreditCardAnalysis(
        cardName = "Axis Atlas Credit Card",
        bankName = "Axis Bank",
        ranking = "#1 Airline & Travel Transfer Card",
        sentiment = "84% Positive • Premium Travel Transfer Tier",
        isPositiveSentiment = true,
        rewardsSummary = "EDGE Miles transferrable 1:2 to Accor, Singapore Airlines & Vistara.",
        annualFee = "₹5,000 + GST",
        joiningBonus = "5,000 EDGE Miles on 1st transaction within 30 days",
        recentChanges = "Milestone tier requirements revised to ₹7.5L for Silver Tier.",
        rewardYieldPercent = "7.2% Travel Yield"
    )
)

@Composable
fun DailyCreditCardSpotlightCard(
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val card = sampleDailyCreditCards[selectedIndex]

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, MinimalPurplePrimary.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_credit_card_spotlight")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Badge & Ranking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MinimalPurplePrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = MinimalPurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DAILY CREDIT CARD SPOTLIGHT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurplePrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = card.ranking,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (card.isPositiveSentiment) Color(0xFF059669).copy(alpha = 0.12f) else Color(0xFFDC2626).copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (card.isPositiveSentiment) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                            contentDescription = null,
                            tint = if (card.isPositiveSentiment) Color(0xFF059669) else Color(0xFFDC2626),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = card.sentiment,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (card.isPositiveSentiment) Color(0xFF059669) else Color(0xFFDC2626)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Card Selector Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sampleDailyCreditCards.forEachIndexed { index, item ->
                    FilterChip(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(item.bankName, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("credit_card_chip_$index")
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MinimalBorder.copy(alpha = 0.5f))
            Spacer(Modifier.height(12.dp))

            // Card Name & Reward Yield Highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = card.cardName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MinimalPurpleLightContainer
                ) {
                    Text(
                        text = card.rewardYieldPercent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinimalPurplePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Rewards Summary
            Text(
                text = "🎁 Rewards Breakdown:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MinimalPurplePrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = card.rewardsSummary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            // Fee & Recent Changes Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Annual Fee: ${card.annualFee}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Latest Rule Change: ${card.recentChanges}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
