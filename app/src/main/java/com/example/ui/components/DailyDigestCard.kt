package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FinancialNewsEntity
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DailyDigestCard(
    newsList: List<FinancialNewsEntity>,
    allNewsList: List<FinancialNewsEntity>,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {}
) {
    val categoryCounts = remember(allNewsList) {
        allNewsList.groupBy { it.category }.mapValues { it.value.size }.toList().sortedByDescending { it.second }
    }
    val maxCount = categoryCounts.maxOfOrNull { it.second } ?: 1
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Top Story") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MinimalPurpleLightContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Market Overview Analytics
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = "Market Overview",
                    tint = MinimalPurplePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Market Overview Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MinimalPurplePrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Total Articles: ${allNewsList.size}",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            categoryCounts.take(5).forEach { (category, count) ->
                val fraction = count.toFloat() / maxCount.toFloat()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onCategoryClick(category) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.labelSmall,
                            color = MinimalPurplePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.05f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MinimalPurplePrimary)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            // Daily Digest
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Daily Digest",
                    tint = MinimalPurplePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daily Digest Top Stories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MinimalPurplePrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            newsList.forEachIndexed { index, news ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            if (!news.sourceUrl.isNullOrBlank()) {
                                webViewUrlToOpen = news.sourceUrl
                                webViewTitleToOpen = news.sourceName
                            }
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MinimalPurplePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurplePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = news.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MinimalPurplePrimary
                        )
                    }
                }
                
                if (index < newsList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 36.dp),
                        color = Color.Black.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
    
    webViewUrlToOpen?.let { url ->
        InAppWebViewDialog(
            url = url,
            title = webViewTitleToOpen,
            onDismiss = { webViewUrlToOpen = null }
        )
    }
}
