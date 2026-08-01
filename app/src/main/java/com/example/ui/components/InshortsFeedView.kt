package com.example.ui.components

import com.example.data.stripIntroductoryLabels
import com.example.data.getMergedOverview
import com.example.data.getMergedKeyTakeaways
import com.example.data.formatToCrispBullets

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.Context
import android.content.Intent
import android.app.Activity
import androidx.compose.material.icons.filled.Refresh
import com.example.utils.AdMobHelper
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Visibility

import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.FinancialNewsEntity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatSocialCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
        else -> count.toString()
    }
}

private fun formatRelativeDate(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun getCategoryBadgeColor(category: String): Color {
    return when (category.lowercase()) {
        "stock market india", "stock market" -> Color(0xFF2E7D32) // Green
        "itr & tax", "tax" -> Color(0xFF6A1B9A) // Purple
        "credit cards" -> Color(0xFFD84315) // Deep Orange
        "loans & fds" -> Color(0xFF1565C0) // Royal Blue
        "mutual funds & sip", "markets & mutual funds" -> Color(0xFF00838F) // Cyan
        "personal finance & savings", "personal finance" -> Color(0xFF00695C) // Teal
        "gst & policy updates", "rbi & policy" -> Color(0xFF4527A0) // Indigo
        "video shorts" -> Color(0xFFC62828) // Deep Red
        else -> MinimalPurplePrimary
    }
}

sealed interface FeedSlide {
    data class NewsSlide(val news: FinancialNewsEntity) : FeedSlide
    data class AdSlide(val slideIndex: Int) : FeedSlide
    data class LeadGenSlide(val slideIndex: Int) : FeedSlide
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InshortsFeedView(
    allNewsList: List<FinancialNewsEntity>,
    dailyDigestList: List<FinancialNewsEntity> = emptyList(),
    categories: List<String>,
    selectedCategory: String,
    playingNewsId: Int?,
    isPlaying: Boolean,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onSelectCategory: (String) -> Unit,
    onPlayAudio: (FinancialNewsEntity) -> Unit,
    onToggleBookmark: (FinancialNewsEntity) -> Unit,
    onOpenComments: ((FinancialNewsEntity) -> Unit)? = null,
    onOpenReader: ((FinancialNewsEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Financial Action") }
    
    val context = LocalContext.current
    val openUrlWithAd = { url: String, title: String ->
        val activity = context as? Activity
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        if (activity != null) {
            AdMobHelper.showInterstitial(activity) {
                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
            }
        } else {
            try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    if (allNewsList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Newspaper,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No articles found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    val displayCategories = if (categories.isEmpty()) listOf("All") else categories
    val initialPage = displayCategories.indexOf(selectedCategory).coerceAtLeast(0)
    val horizontalPagerState = rememberPagerState(initialPage = initialPage, pageCount = { displayCategories.size })
    
    LaunchedEffect(horizontalPagerState.currentPage) {
        val cat = displayCategories[horizontalPagerState.currentPage]
        if (cat != selectedCategory) {
            onSelectCategory(cat)
        }
    }
    
    LaunchedEffect(selectedCategory) {
        val idx = displayCategories.indexOf(selectedCategory)
        if (idx != -1 && idx != horizontalPagerState.currentPage) {
            horizontalPagerState.scrollToPage(idx)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentCat = displayCategories[page]
            val catNewsList = remember(allNewsList, currentCat) {
                if (currentCat == "All") allNewsList
                else allNewsList.filter { news ->
                    news.category.equals(currentCat, ignoreCase = true) ||
                    news.category.contains(currentCat, ignoreCase = true) ||
                    (currentCat == "Card Hacks & Perks" && (news.category.contains("Card", ignoreCase = true) || news.title.contains("Card", ignoreCase = true) || news.title.contains("Reward", ignoreCase = true))) ||
                    (currentCat == "Market Signals" && (news.category.contains("Market", ignoreCase = true) || news.category.contains("Signal", ignoreCase = true) || news.category.contains("Mutual", ignoreCase = true) || news.category.contains("Stock", ignoreCase = true) || news.title.contains("Nifty", ignoreCase = true) || news.title.contains("Sensex", ignoreCase = true) || news.title.contains("SIP", ignoreCase = true))) ||
                    (currentCat == "Tech & AI" && (news.category.contains("Tech", ignoreCase = true) || news.category.contains("AI", ignoreCase = true) || news.category.contains("Gaming", ignoreCase = true) || news.category.contains("Car", ignoreCase = true) || news.category.contains("EV", ignoreCase = true))) ||
                    (currentCat == "Startup & Capital" && (news.category.contains("Startup", ignoreCase = true) || news.category.contains("Capital", ignoreCase = true) || news.category.contains("D2C", ignoreCase = true) || news.category.contains("Funding", ignoreCase = true) || news.title.contains("Founder", ignoreCase = true))) ||
                    (currentCat == "Wealth 101" && (news.category.contains("Wealth", ignoreCase = true) || news.category.contains("Finance", ignoreCase = true) || news.category.contains("Tax", ignoreCase = true) || news.category.contains("Education", ignoreCase = true) || news.category.contains("Crypto", ignoreCase = true) || news.category.contains("Loan", ignoreCase = true) || news.category.contains("FD", ignoreCase = true)))
                }
            }
            val displayNewsList = catNewsList

            val interleavedSlides = remember(displayNewsList, dailyDigestList) {
                val slides = mutableListOf<FeedSlide>()
                
                var slideCounter = slides.size
                for (news in displayNewsList) {

                    slides.add(FeedSlide.NewsSlide(news))
                    slideCounter++
                }
                slides
            }

            val verticalPagerState = rememberPagerState(pageCount = { interleavedSlides.size })
            
            var autoSwipeEnabled by remember { mutableStateOf(true) }
            var swipeIntervalMs by remember { mutableStateOf(10000L) }
            var timeRemainingMs by remember { mutableStateOf(10000L) }
            
            LaunchedEffect(verticalPagerState.currentPage, autoSwipeEnabled, swipeIntervalMs, currentCat) {
                if (autoSwipeEnabled && verticalPagerState.currentPage < interleavedSlides.size - 1) {
                    timeRemainingMs = swipeIntervalMs
                    while (timeRemainingMs > 0) {
                        kotlinx.coroutines.delay(1000)
                        timeRemainingMs -= 1000
                    }
                    verticalPagerState.animateScrollToPage(verticalPagerState.currentPage + 1)
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().testTag("inshorts_feed_view_refresh_$currentCat")
            ) {
                VerticalPager(
                    state = verticalPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { vPage ->
                    when (val slide = interleavedSlides[vPage]) {

                        is FeedSlide.NewsSlide -> {
                            val news = slide.news
                            val isCurrentPlaying = isPlaying && playingNewsId == news.id
                            InshortsNewsCardItem(
                                news = news,
                                isPlaying = isCurrentPlaying,
                                pageIndex = vPage,
                                totalPages = interleavedSlides.size,
                                onPlayAudio = { onPlayAudio(news) },
                                onToggleBookmark = { onToggleBookmark(news) },
                                onOpenActionUrl = { url ->
                                    openUrlWithAd(url, news.title)
                                },
                                onOpenComments = if (onOpenComments != null) { { onOpenComments(news) } } else null,
                                onOpenReader = if (onOpenReader != null) { { onOpenReader(news) } } else null
                            )
                        }
                        else -> {}
                        
                        
                    }
                }
            }
        }

        // Top Category Filter Chips Overlay
        if (categories.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MinimalPurpleLightContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Transparent,
                                selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = Color.Transparent,
                                labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.Transparent,
                                selectedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.testTag("inshorts_category_$category")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InshortsNewsCardItem(
    news: FinancialNewsEntity,
    isPlaying: Boolean,
    pageIndex: Int,
    totalPages: Int,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenActionUrl: (String) -> Unit,
    onOpenComments: (() -> Unit)? = null,
    onOpenReader: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val fallbackImage = news.imageUrl ?: when (news.category) {
        "Card Hacks & Perks" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"
        "Market Signals" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"
        "Tech & AI" -> "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1200&q=80"
        "Startup & Capital" -> "https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1200&q=80"
        else -> "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80"
    }

    val imageUrlToDisplay = news.imageUrl ?: fallbackImage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("inshorts_card_${news.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // News Content Body (Starts right at the top)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 20.dp, end = 20.dp, top = 72.dp, bottom = 16.dp)
            ) {
                // Header Category Pill, Page Counter & Source Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = getCategoryBadgeColor(news.category),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = news.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        // Page Index Tag
                        Surface(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${pageIndex + 1} / $totalPages",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalPurpleLightContainer
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${news.sourceName} • ${formatRelativeDate(news.publishedAt)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clean Bold Headline Title
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Key Takeaway Tag Chip
                val takeawayTag = news.summaryActionableTakeaway.take(90)
                if (takeawayTag.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Takeaway: $takeawayTag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bulleted Impact & Financial Analysis Points
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InshortsBulletPoint(
                        icon = Icons.Default.Newspaper,
                        iconColor = MaterialTheme.colorScheme.primary,
                        label = "Overview",
                        content = news.getMergedOverview()
                    )
                    
                    val keyTakeaways = news.getMergedKeyTakeaways()
                    if (keyTakeaways.isNotBlank()) {
                        InshortsBulletPoint(
                            icon = Icons.Default.CheckCircle,
                            iconColor = MaterialTheme.colorScheme.primary,
                            label = "Key Takeaways",
                            content = keyTakeaways
                        )
                    }
                    
                    ExpertTwitterSentimentWidget(category = news.category, title = news.title)
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Section: Metrics & Actions
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))
                    WhatsChangedIndicator(news)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Icons (Views, Bookmark, Share, Chat)
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp), verticalAlignment = Alignment.CenterVertically) {
                            
                            // Views
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Reads",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatSocialCount(news.readCount),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                )
                            }
                            
                            IconButton(
                                onClick = onToggleBookmark,
                                modifier = Modifier.size(36.dp).testTag("inshorts_bookmark_${news.id}")
                            ) {
                                Icon(
                                    imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (news.isBookmarked) MinimalPurpleLightContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            // Share
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 4.dp)
                                    .clickable {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, news.title)
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "⚡ ${news.title}\n\nKey Takeaway: ${news.summaryActionableTakeaway}\n\nRead 60-sec update: ${news.sourceUrl}"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
                                    }
                                    .testTag("inshorts_share_${news.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Article",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatSocialCount(news.shareCount),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                )
                            }
                            
                            if (onOpenComments != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .padding(horizontal = 4.dp)
                                        .clickable { onOpenComments() }
                                        .testTag("inshorts_comments_${news.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Comments",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatSocialCount(news.commentCount),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val actionUrl = news.sourceUrl
                            Button(
                                onClick = {
                                    if (onOpenReader != null) {
                                        onOpenReader()
                                    } else {
                                        onOpenActionUrl(actionUrl)
                                    }
                                },
                                modifier = Modifier
                                    .height(40.dp)
                                    .testTag("inshorts_apply_button_${news.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MinimalPurplePrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Launch,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Read In-App",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Audio FAB Player
        FloatingActionButton(
            onClick = onPlayAudio,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 72.dp, end = 20.dp)
                .testTag("inshorts_audio_fab_${news.id}"),
            containerColor = if (isPlaying) MinimalPurpleDark else MinimalPurplePrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Stream 60s Audio Summary",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun InshortsBulletPoint(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    content: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = iconColor,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.95f)
                    )
                )
            }
        }
    }
}


data class TwitterSentimentInfo(
    val handle: String,
    val name: String,
    val tweetText: String,
    val sentimentBadge: String,
    val sentimentColor: Color
)

@Composable
fun ExpertTwitterSentimentWidget(category: String, title: String) {
    val info = remember(category, title) {
        when {
            category.contains("Card", ignoreCase = true) -> TwitterSentimentInfo(
                "@CreditGuruIndia", "Credit Insights India",
                "Maximizing milestone benefits and reward multipliers on this update yields an effective net cash back return of ~7.5%. Smart move before quarterly fee revisions! 💳✨",
                "🟢 Bullish Card Value", Color(0xFF4CAF50)
            )
            category.contains("Market", ignoreCase = true) || category.contains("Signal", ignoreCase = true) -> TwitterSentimentInfo(
                "@MarketAnalyst99", "Stock Market Pulse",
                "Nifty holding key support levels around major moving averages. Institutional buying in banking and index leaders indicates strong short-term momentum. 📈",
                "🚀 Strong Market Sentiment", Color(0xFF2196F3)
            )
            category.contains("Tech", ignoreCase = true) || category.contains("AI", ignoreCase = true) -> TwitterSentimentInfo(
                "@TechInsider_IN", "Tech & AI Briefs",
                "Enterprise adoption of generative AI and automated tax/finance workflows is accelerating 40% YoY across Indian tech hubs. Huge efficiency upside! 🚀",
                "⚡ High Growth Impact", Color(0xFF9C27B0)
            )
            category.contains("Startup", ignoreCase = true) || category.contains("Capital", ignoreCase = true) -> TwitterSentimentInfo(
                "@VenturePulseIN", "Venture Pulse India",
                "D2C founders focusing on unit economics and cash flow positivity rather than burn-heavy expansion are seeing rapid institutional capital backing. 💼",
                "📈 Positive VC Outlook", Color(0xFFFF9800)
            )
            else -> TwitterSentimentInfo(
                "@TaxGuru_In", "Tax Wise Desk",
                "New tax regime slab optimizations and digital filing protocols save up to ₹78,000 annually for high-earning professionals. Review deductions now! 📊",
                "💡 High Tax Saving Potential", Color(0xFF009688)
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = info.name.take(1),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = info.name,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Expert",
                                tint = Color(0xFF1DA1F2),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = "${info.handle} • 2h ago",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = info.sentimentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = info.sentimentBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = info.sentimentColor),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = info.tweetText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
            )
        }
    }
}


