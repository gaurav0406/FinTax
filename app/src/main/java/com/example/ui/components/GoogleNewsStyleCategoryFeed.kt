package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.MinimalBorder

@Composable
fun GoogleNewsStyleCategoryFeed(
    allNewsList: List<FinancialNewsEntity>,
    onArticleClick: (FinancialNewsEntity) -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group by category, keep only categories with at least 1 item
    val groupedNews = remember(allNewsList) {
        allNewsList.groupBy { it.category }
            .filter { it.value.isNotEmpty() }
            .toList()
            .sortedByDescending { it.second.size } // categories with more news first
    }

    if (groupedNews.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Explore by Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(groupedNews) { (category, articles) ->
                GoogleNewsCategoryColumn(
                    category = category,
                    articles = articles.take(3), // Max 3 per column
                    onArticleClick = onArticleClick,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}

@Composable
fun GoogleNewsCategoryColumn(
    category: String,
    articles: List<FinancialNewsEntity>,
    onArticleClick: (FinancialNewsEntity) -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MinimalBorder.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        modifier = modifier.width(300.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategoryClick(category) }
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View Category",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Articles
            articles.forEachIndexed { index, article ->
                GoogleNewsCompactItem(
                    article = article,
                    onClick = { onArticleClick(article) }
                )
                if (index < articles.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MinimalBorder.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleNewsCompactItem(
    article: FinancialNewsEntity,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Relative time logic (simplified for mockup)
    val timeStr = remember(article.publishedAt) {
        val diff = System.currentTimeMillis() - article.publishedAt
        val hours = diff / (1000 * 60 * 60)
        if (hours < 1) "Just now"
        else if (hours < 24) "$hours hours ago"
        else "${hours / 24} days ago"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Left Content (Source, Title, Time, Context Icon)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            // Source Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(16.dp)
                ) {
                    // Placeholder for publisher favicon
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = article.sourceName ?: "News Source",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Title
            Text(
                text = article.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Footer (Time & Options)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // "Full Coverage" / Context action icon
                Icon(
                    imageVector = Icons.Default.MoreVert, // Using MoreVert as a placeholder for the layer icon
                    contentDescription = "More options",
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .padding(2.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Right Image
        val imageUrl = article.imageUrl
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}
