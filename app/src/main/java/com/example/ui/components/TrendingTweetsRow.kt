package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class MockTweet(val handle: String, val content: String, val time: String)

val sampleTweets = listOf(
    MockTweet("@FinTwit_IN", "RBI holds repo rate steady. What does it mean for your home loans? Expect rates to remain stable for now! #RBI #HomeLoan", "2h"),
    MockTweet("@TaxUpdates", "Reminder: Advance tax deadline is approaching. Ensure you pay the installment to avoid interest under Section 234C. #IncomeTax #India", "4h"),
    MockTweet("@MarketGossip", "Nifty breaches new highs! IT and Banking stocks leading the rally today. Bull run continues? 🚀 #StockMarket #Nifty50", "5h"),
    MockTweet("@TechStartupsIN", "AI startup funding triples this quarter. Generative AI is reshaping the landscape of Indian tech. #AI #Startups", "6h")
)

@Composable
fun TrendingTweetsRow() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            text = "Trending on FinTwit",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleTweets) { tweet ->
                Card(
                    modifier = Modifier.width(280.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MinimalPurpleLightContainer),
                            border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tweet.handle,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MinimalPurplePrimary)
                            )
                            Text(
                                text = tweet.time,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tweet.content,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            color = TextPrimary,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                            Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
