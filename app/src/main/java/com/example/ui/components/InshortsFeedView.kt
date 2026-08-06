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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Lightbulb
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
    val openUrlWithAd = remember(context) {
        { url: String, title: String ->
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
    var showAdDialog by remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showAdDialog) {
        AlertDialog(
            onDismissRequest = { showAdDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = MinimalPurplePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sponsored Ad & Offer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🌟 ICICI Direct Wealth Pass / Groww Special Offer", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Zero brokerage equity delivery & 0% interest intraday margin for active investors. Claim exclusive rewards now!", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Transitioning to news article...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAdDialog = false
                        if (onOpenReader != null) {
                            onOpenReader()
                        } else {
                            onOpenActionUrl(news.sourceUrl)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)
                ) {
                    Text("Continue to Article")
                }
            }
        )
    }

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

                // Crisp 8-10 words Headline Title
                val crispTitle = remember(news.title) {
                    val words = news.title.split(Regex("\\s+"))
                    if (words.size > 9) words.take(9).joinToString(" ") + "..." else news.title
                }
                Text(
                    text = crispTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Conditional Source Image Tile (No placeholder fallback when absent)
                if (!news.imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = news.imageUrl,
                        contentDescription = news.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Container for Why Read This, Overview, Key Takeaways, What's Changed, Ad, and Twitter widget
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Prominent "Why Read This" Tag Badge
                    val whyReadText = news.summaryActionableTakeaway.ifBlank { news.summaryWhoImpacted }.ifBlank { news.summaryWhatHappened }.take(180)
                    if (whyReadText.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "WHY READ THIS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.tertiary,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                    Text(
                                        text = whyReadText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

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

                    WhyItMattersWidget(
                        news = news
                    )

                    WhatsChangedIndicator(news)

                    // Sponsored Ad Banner
                    InshortsAdBanner()
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Section: Actions
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp)
                ) {
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
                                    showAdDialog = true
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = iconColor,
                        letterSpacing = 0.6.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun InshortsAdBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SPONSORED AD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.8.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Promoted",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ICICI Direct Wealth Pass: Zero Brokerage & 0% Margin. Claim Rewards Now!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun WhyItMattersWidget(
    news: FinancialNewsEntity
) {
    val whyItMattersText = news.whyItMatters?.takeIf { it.isNotBlank() } ?: when {
        news.category.contains("Card", ignoreCase = true) -> "• Maximizes annual cash back and reward point redemption across dining and travel.\n• Unlocks milestone spending bonuses before quarterly fee revisions."
        news.category.contains("Market", ignoreCase = true) || news.category.contains("Signal", ignoreCase = true) -> "• Enhances liquidity settlement speed and reduces portfolio holding costs.\n• Unlocks compounding alpha through disciplined systematic investment plans."
        news.category.contains("Tech", ignoreCase = true) || news.category.contains("AI", ignoreCase = true) -> "• Accelerates enterprise AI adoption and automated workflow efficiency.\n• Lowers operational overhead across engineering and product teams."
        news.category.contains("Startup", ignoreCase = true) || news.category.contains("Capital", ignoreCase = true) -> "• Focuses on unit economics and cash flow positivity for VCs.\n• Drives rapid institutional capital backing and sustainable growth."
        else -> "• Significantly influences asset allocation and net annual savings strategies.\n• Optimizes long-term financial planning and wealth accumulation goals."
    }

    InshortsBulletPoint(
        icon = Icons.Default.Info,
        iconColor = MaterialTheme.colorScheme.primary,
        label = "Why It Matters",
        content = whyItMattersText
    )
}


