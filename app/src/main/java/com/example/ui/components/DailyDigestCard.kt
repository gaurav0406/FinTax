package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Sync
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyDigestCard(
    newsList: List<FinancialNewsEntity>,
    allNewsList: List<FinancialNewsEntity>,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {}
) {
    val totalCount = allNewsList.size
    val categoryCounts = remember(allNewsList) {
        allNewsList.groupBy { it.category }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
    }
    val maxCount = categoryCounts.maxOfOrNull { it.second } ?: 1

    val latestPublishedAt = remember(allNewsList) {
        allNewsList.maxOfOrNull { it.publishedAt } ?: System.currentTimeMillis()
    }
    
    val lastUpdatedFormatted = remember(latestPublishedAt) {
        if (latestPublishedAt <= 0) "Just now"
        else {
            val diffMs = System.currentTimeMillis() - latestPublishedAt
            val mins = diffMs / (1000 * 60)
            if (mins < 1) "Just now"
            else if (mins < 60) "$mins mins ago"
            else {
                val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                sdf.format(Date(latestPublishedAt))
            }
        }
    }

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
            // Header with Live Sync Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MinimalPurplePrimary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2E7D32)) // Active green dot
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live Sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = MinimalPurplePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tracked Articles: $totalCount across ${categoryCounts.size} sectors",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Updated: $lastUpdatedFormatted",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            categoryCounts.take(5).forEach { (category, count) ->
                val fraction = count.toFloat() / maxCount.toFloat()
                val percentage = if (totalCount > 0) (count * 100 / totalCount) else 0

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
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$count ($percentage%)",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
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
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            // Daily Digest Top Stories Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MinimalPurplePrimary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "Preferred Feeds",
                        style = MaterialTheme.typography.labelSmall,
                        color = MinimalPurplePrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

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
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                lineHeight = 22.sp
                            ),
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = news.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
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
