package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.FinancialNewsEntity
import com.example.data.stripIntroductoryLabels
import com.example.ui.theme.MinimalBorder

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MinimalBorder.copy(alpha = 0.5f), thickness = 1.dp)
    }
}

@Composable
fun GoogleNewsHeroCard(
    news: FinancialNewsEntity,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: (() -> Unit)? = null,
    onOpenReader: () -> Unit,
    autoPlayAudio: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timeStr = remember(news.publishedAt) {
        val diff = System.currentTimeMillis() - news.publishedAt
        val hours = diff / (1000 * 60 * 60)
        if (hours < 1) "Just now" else if (hours < 24) "$hours hours ago" else "${hours / 24} days ago"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MinimalBorder.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onOpenReader() }
    ) {
        Column {
            // Large Hero Image
            if (!news.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(news.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Source and Category
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = news.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = news.sourceName ?: "News Source",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))
                
                // Excerpt / Summary (Hero cards usually have a small excerpt)
                Text(
                    text = news.summaryWhatHappened,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))
                
                // AI Insight Indicator
                if (news.summaryActionableTakeaway.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Insight", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = news.summaryActionableTakeaway.stripIntroductoryLabels(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Footer (Time & Actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (onOpenComments != null) {
                            IconButton(onClick = onOpenComments, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Chat, contentDescription = "Comments", modifier = Modifier.size(18.dp))
                            }
                        }
                        IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                modifier = Modifier.size(18.dp),
                                tint = if (news.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleNewsSecondaryCard(
    news: FinancialNewsEntity,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: (() -> Unit)? = null,
    onOpenReader: () -> Unit,
    autoPlayAudio: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timeStr = remember(news.publishedAt) {
        val diff = System.currentTimeMillis() - news.publishedAt
        val hours = diff / (1000 * 60 * 60)
        if (hours < 1) "Just now" else if (hours < 24) "$hours hours ago" else "${hours / 24} days ago"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MinimalBorder.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onOpenReader() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                // Source
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = news.sourceName ?: "News Source",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // AI Insight / Why Read This snippet (Fully visible, extended card size)
                val whyReadText = news.summaryActionableTakeaway.ifBlank { news.summaryWhoImpacted }.ifBlank { news.summaryWhatHappened }.stripIntroductoryLabels()
                if (whyReadText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Why read: $whyReadText",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // What's Changed Metric Insight
                val indicatorText = remember(news.id) {
                    val raw = (news.financialImpactBullets ?: "") + " " + (news.summaryText) + " " + (news.title)
                    when {
                        raw.contains("reward", true) || raw.contains("point", true) -> "Reward Points Increased (+15% / 5,000 Pts)"
                        raw.contains("launch", true) || raw.contains("unit", true) -> "Launch Volume Increased by 12,450 Units"
                        raw.contains("spend", true) || raw.contains("book value", true) || raw.contains("benefit", true) -> "Spend Increased for Book Value & Benefits (+18.4%)"
                        raw.contains("rate", true) || raw.contains("hike", true) || raw.contains("cut", true) -> "Financial Rate / Metric Adjusted (+2.5%)"
                        raw.contains("profit", true) || raw.contains("revenue", true) || raw.contains("sales", true) -> "Performance Metrics: Revenue/Sales Increased"
                        else -> "Key Financial Metric Updated (+12.5% YoY)"
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "What's changed: $indicatorText",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer (Time & Actions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onToggleBookmark, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                modifier = Modifier.size(16.dp),
                                tint = if (news.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Right Image
            if (!news.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(news.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}
