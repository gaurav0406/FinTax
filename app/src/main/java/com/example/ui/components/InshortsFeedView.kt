package com.example.ui.components

import com.example.data.stripIntroductoryLabels

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

import androidx.compose.material3.CircularProgressIndicator
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
    data class DailyDigestSlide(val newsList: List<FinancialNewsEntity>) : FeedSlide
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
    modifier: Modifier = Modifier
) {
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Financial Action") }
    
    val context = LocalContext.current
    val openUrlWithAd = { url: String, title: String ->
        val activity = context as? Activity
        if (activity != null) {
            AdMobHelper.showInterstitial(activity) {
                webViewUrlToOpen = url
                webViewTitleToOpen = title
            }
        } else {
            webViewUrlToOpen = url
            webViewTitleToOpen = title
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
                    (currentCat == "Credit Cards" && (news.category.contains("Card", ignoreCase = true) || news.title.contains("Card", ignoreCase = true))) ||
                    (currentCat == "ITR & Tax" && (news.category.contains("Tax", ignoreCase = true) || news.category.contains("ITR", ignoreCase = true) || news.title.contains("Tax", ignoreCase = true) || news.title.contains("ITR", ignoreCase = true))) ||
                    (currentCat == "Loans & FDs" && (news.category.contains("Loan", ignoreCase = true) || news.category.contains("FD", ignoreCase = true) || news.title.contains("Loan", ignoreCase = true) || news.title.contains("FD", ignoreCase = true))) ||
                    (currentCat == "Markets & Mutual Funds" && (news.category.contains("Mutual", ignoreCase = true) || news.category.contains("Stock", ignoreCase = true) || news.category.contains("Fund", ignoreCase = true) || news.title.contains("Nifty", ignoreCase = true) || news.title.contains("Sensex", ignoreCase = true) || news.title.contains("SIP", ignoreCase = true) || news.title.contains("IPO", ignoreCase = true))) ||
                    (currentCat == "RBI & Policy" && (news.category.contains("RBI", ignoreCase = true) || news.category.contains("Policy", ignoreCase = true) || news.title.contains("RBI", ignoreCase = true) || news.title.contains("Repo", ignoreCase = true))) ||
                    (currentCat == "Crypto" && (news.category.contains("Crypto", ignoreCase = true) || news.title.contains("Bitcoin", ignoreCase = true) || news.title.contains("Crypto", ignoreCase = true)))
                }
            }
            val displayNewsList = catNewsList

            val interleavedSlides = remember(displayNewsList, dailyDigestList) {
                val slides = mutableListOf<FeedSlide>()
                if (dailyDigestList.isNotEmpty() && currentCat == "All") {
                    slides.add(FeedSlide.DailyDigestSlide(dailyDigestList))
                }
                var slideCounter = slides.size
                for (news in displayNewsList) {
                    if (slideCounter > 0 && (slideCounter + 1) % 4 == 0 && (slideCounter + 1) % 8 != 0) {
                        slides.add(FeedSlide.AdSlide(slideCounter))
                        slideCounter++
                    }
                    if (slideCounter > 0 && (slideCounter + 1) % 8 == 0) {
                        slides.add(FeedSlide.LeadGenSlide(slideCounter))
                        slideCounter++
                    }
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
                        is FeedSlide.DailyDigestSlide -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                DailyDigestCard(
                                    newsList = slide.newsList,
                                    allNewsList = allNewsList,
                                    onCategoryClick = onSelectCategory
                                )
                            }
                        }
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
                                onOpenComments = if (onOpenComments != null) { { onOpenComments(news) } } else null
                            )
                        }
                        is FeedSlide.AdSlide -> {
                            AdMobNativeExpressCard(
                                slideIndex = vPage,
                                onOpenAd = { url ->
                                    openUrlWithAd(url, "Sponsored Partner")
                                }
                            )
                        }
                        is FeedSlide.LeadGenSlide -> {
                            LeadGenerationCard(
                                slideIndex = vPage,
                                onOpenExternalLink = { url ->
                                    openUrlWithAd(url, "Card Application Portal")
                                }
                            )
                        }
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

        // Open In-App WebView Dialog if URL selected
        webViewUrlToOpen?.let { url ->
            InAppWebViewDialog(
                url = url,
                title = webViewTitleToOpen,
                onDismiss = { webViewUrlToOpen = null }
            )
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
    onOpenComments: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val fallbackImage = when (news.category) {
        "Financial News" -> "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80"
        "Credit Cards" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"
        "Loans & FDs" -> "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80"
        "Markets & Mutual Funds" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"
        else -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80"
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

                Spacer(modifier = Modifier.height(16.dp))                // 4 Bulleted Impact & Financial Analysis Points
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InshortsBulletPoint(
                        icon = Icons.Default.Newspaper,
                        iconColor = MaterialTheme.colorScheme.primary,
                        label = "Summary",
                        content = news.summaryWhatHappened.stripIntroductoryLabels()
                    )
                    
                    InshortsBulletPoint(
                        icon = Icons.Default.Info,
                        iconColor = MaterialTheme.colorScheme.primary,
                        label = "Why It Matters",
                        content = news.summaryText.stripIntroductoryLabels()
                    )
                    val calculatedImpact = news.financialImpactBullets ?: if (news.isFinancialCategory) {
                        when (news.category) {
                            "Financial News" -> "• Regulatory policy shift impacting sector valuations by ~2.5%\n• Capital allocation adjustment advised to optimize net return"
                            "Credit Cards" -> "• Utility fee caps adjusted by -₹350/mo or +5% fuel waiver benefit\n• Optimized annual cashback yield estimated at ₹4,800/yr"
                            "Loans & FDs" -> "• Interest Yield: 8.25% p.a. (+₹8,250/yr per ₹1L deposit)\n• Loan EMI Impact: +₹320/mo on ₹50L Home Loan reset"
                            "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees funds 48 hrs faster\n• Expected Yield: +1.2% CAGR boost from faster reinvestment"
                            "Cars & EV" -> "• Operational Savings: ~₹7,000/mo (₹84,000/yr) vs Petrol vehicle\n• Tax Incentive: Sec 80EEB tax deduction up to ₹1.5 Lakhs"
                            else -> "• Financial Gain: Estimated ₹5,000 - ₹12,000 annual net benefit by optimizing financial options."
                        }
                    } else {
                        when (news.category) {
                            "Sports" -> "• Championship Standing: India leads WTC table with strong performance\n• Key Highlight: Record-breaking performance in recent fixtures"
                            "Education" -> "• Curriculum Shift: Dual-board exam structure & updated entrance syllabi\n• Practical Takeaway: Skill integration across vocational streams"
                            "Entertainment" -> "• Streaming Rights: Major platform licensing and high viewer engagement\n• Audience Value: Broader access to premium digital content bundles"
                            "Technology Insights" -> "• Infrastructure Boost: Domestic manufacturing expansion and supply chain growth\n• Tech Efficiency: Lower reliance on component imports"
                            "AI & New Happenings" -> "• Workflow Automation: Accelerated developer productivity & AI deployment\n• Career Advantage: High demand for generative AI skills"
                            else -> "• Core operational developments affecting market performance\n• Strategic findings for long-term planning"
                        }
                    }
                    InshortsBulletPoint(
                        icon = if (news.isFinancialCategory) Icons.Default.Calculate else Icons.Default.Info,
                        iconColor = MaterialTheme.colorScheme.primary,
                        label = "Financial Impact & Benefits",
                        content = calculatedImpact.stripIntroductoryLabels()
                    )
                    InshortsBulletPoint(
                        icon = Icons.Default.CheckCircle,
                        iconColor = MaterialTheme.colorScheme.primary,
                        label = "Actionable Takeaways",
                        content = news.summaryActionableTakeaway.stripIntroductoryLabels()
                    )
                    
                    if (news.category.contains("Market", ignoreCase = true) || news.category.contains("Funds", ignoreCase = true)) {
                        BullishBearishWidget(newsId = news.id)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Section: Metrics & Actions
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp)
                ) {

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
                        val actionUrl = news.financialActionUrl ?: news.sourceUrl
                        Button(
                            onClick = { onOpenActionUrl(actionUrl) },
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("inshorts_apply_button_${news.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MinimalPurplePrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Launch,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Learn More",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
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
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.95f)
                    )
                )
            }
        }
    }
}


@Composable
fun BullishBearishWidget(newsId: Int) {
    var hasVoted by remember { mutableStateOf(false) }
    var bullishVotes by remember { mutableStateOf(124) }
    var bearishVotes by remember { mutableStateOf(45) }
    
    val totalVotes = bullishVotes + bearishVotes
    val bullishPercent = if (totalVotes > 0) (bullishVotes.toFloat() / totalVotes * 100).toInt() else 0
    val bearishPercent = if (totalVotes > 0) 100 - bullishPercent else 0

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = "Community Sentiment",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!hasVoted) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { bullishVotes++; hasVoted = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Bullish 🚀")
                }
                Button(
                    onClick = { bearishVotes++; hasVoted = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Bearish 📉")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(bullishPercent.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$bullishPercent%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(bearishPercent.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(Color(0xFFF44336)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$bearishPercent%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bullish", fontSize = 10.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                Text("Bearish", fontSize = 10.sp, color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
            }
        }
    }
}


