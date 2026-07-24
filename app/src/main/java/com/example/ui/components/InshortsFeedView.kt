package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

sealed interface FeedSlide {
    data class NewsSlide(val news: FinancialNewsEntity) : FeedSlide
    data class AdSlide(val slideIndex: Int) : FeedSlide
    data class LeadGenSlide(val slideIndex: Int) : FeedSlide
    data class DailyDigestSlide(val newsList: List<FinancialNewsEntity>) : FeedSlide
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InshortsFeedView(
    newsList: List<FinancialNewsEntity>,
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

    if (newsList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F12)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Newspaper,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No articles found in $selectedCategory",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        return
    }

    // Build Interleaved Slides (AdMob Native every 4th slide, Lead Gen every 8th slide)
    val interleavedSlides = remember(newsList, dailyDigestList) {
        val slides = mutableListOf<FeedSlide>()
        if (dailyDigestList.isNotEmpty()) {
            slides.add(FeedSlide.DailyDigestSlide(dailyDigestList))
        }
        var slideCounter = slides.size

        for (news in newsList) {
            // Every 4th slide insert AdMob Native Express Card
            if (slideCounter > 0 && (slideCounter + 1) % 4 == 0 && (slideCounter + 1) % 8 != 0) {
                slides.add(FeedSlide.AdSlide(slideCounter))
                slideCounter++
            }

            // Every 8th slide insert Lead Generation Card
            if (slideCounter > 0 && (slideCounter + 1) % 8 == 0) {
                slides.add(FeedSlide.LeadGenSlide(slideCounter))
                slideCounter++
            }

            slides.add(FeedSlide.NewsSlide(news))
            slideCounter++
        }
        slides
    }

    val pagerState = rememberPagerState(pageCount = { interleavedSlides.size })

    var autoSwipeEnabled by remember { mutableStateOf(true) }
    var swipeIntervalMs by remember { mutableStateOf(10000L) }
    var timeRemainingMs by remember { mutableStateOf(10000L) }

    LaunchedEffect(pagerState.currentPage, autoSwipeEnabled, swipeIntervalMs) {
        if (autoSwipeEnabled && pagerState.currentPage < interleavedSlides.size - 1) {
            timeRemainingMs = swipeIntervalMs
            while (timeRemainingMs > 0) {
                kotlinx.coroutines.delay(1000)
                timeRemainingMs -= 1000
            }
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .testTag("inshorts_feed_view")
    ) {
        // Vertical Pager Swipe View
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (val slide = interleavedSlides[page]) {
                is FeedSlide.DailyDigestSlide -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        DailyDigestCard(
                            newsList = slide.newsList,
                            allNewsList = newsList,
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
                        pageIndex = page,
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
                        slideIndex = page,
                        onOpenAd = { url ->
                            openUrlWithAd(url, "Sponsored Partner")
                        }
                    )
                }

                is FeedSlide.LeadGenSlide -> {
                    LeadGenerationCard(
                        slideIndex = page,
                        onOpenExternalLink = { url ->
                            openUrlWithAd(url, "Card Application Portal")
                        }
                    )
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
                color = Color.Black.copy(alpha = 0.6f)
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
                                selectedContainerColor = MinimalPurplePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.12f),
                                labelColor = Color.White.copy(alpha = 0.8f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.White.copy(alpha = 0.2f),
                                selectedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.testTag("inshorts_category_$category")
                        )
                    }
                }
            }
        }

        // Swipe hint indicator at top right
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 56.dp, end = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = MinimalPurpleLightContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Swipe up for next",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (autoSwipeEnabled) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Auto-swipe in ${timeRemainingMs / 1000}s",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "Stop",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = MinimalPurplePrimary,
                                modifier = Modifier
                                    .clickable { autoSwipeEnabled = false }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (swipeIntervalMs == 10000L) {
                                Text(
                                    text = "20s",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = MinimalPurplePrimary,
                                    modifier = Modifier
                                        .clickable {
                                            swipeIntervalMs = 20000L
                                            timeRemainingMs = 20000L
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            } else {
                                Text(
                                    text = "10s",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = MinimalPurplePrimary,
                                    modifier = Modifier
                                        .clickable {
                                            swipeIntervalMs = 10000L
                                            timeRemainingMs = 10000L
                                        }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
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
        "Credit Cards" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"
        "ITR & Tax" -> "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=1200&q=80"
        "Loans & FDs" -> "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80"
        "Markets & Mutual Funds" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"
        else -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80"
    }

    val imageUrlToDisplay = news.imageUrl ?: fallbackImage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
            .testTag("inshorts_card_${news.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Upper Image Section with Bottom Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrlToDisplay)
                        .crossfade(true)
                        .build(),
                    contentDescription = news.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    Color(0xFF0F0F12)
                                )
                            )
                        )
                )

                // Page Index Tag
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 12.dp),
                    color = Color.Black.copy(alpha = 0.6f),
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

            // News Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Header Category Pill & Source Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MinimalPurplePrimary,
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = news.sourceName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("inshorts_bookmark_${news.id}")
                        ) {
                            Icon(
                                imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (news.isBookmarked) MinimalPurpleLightContainer else Color.White.copy(alpha = 0.7f)
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, news.title)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "⚡ ${news.title}\n\nKey Takeaway: ${news.summaryActionableTakeaway}\n\nRead 60-sec update: ${news.sourceUrl}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("inshorts_share_${news.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Article",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        if (onOpenComments != null) {
                            IconButton(
                                onClick = onOpenComments,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("inshorts_comments_${news.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Comments",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Clean Bold Headline Title (20pt/sp)
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        color = Color.White
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Bulleted Impact Points
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InshortsBulletPoint(
                        icon = Icons.Default.Group,
                        iconColor = Color(0xFF81D4FA),
                        label = "Who is Impacted",
                        content = news.summaryWhoImpacted
                    )

                    if (news.category in listOf("ITR & Tax", "Loans & FDs", "Credit Cards", "Tax")) {
                        InshortsBulletPoint(
                            icon = Icons.Default.Info,
                            iconColor = Color(0xFFFFD54F),
                            label = "How You're Impacted (Tangible/Intangible)",
                            content = news.summaryWhatHappened
                        )

                        InshortsBulletPoint(
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFFA5D6A7),
                            label = "Action To Take (Avoid Risk & Maximize Benefits)",
                            content = news.summaryActionableTakeaway
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons at Bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val actionUrl = news.financialActionUrl ?: news.sourceUrl
                    Button(
                        onClick = { onOpenActionUrl(actionUrl) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("inshorts_apply_button_${news.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MinimalPurplePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Apply / Learn More",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = { onOpenActionUrl(news.sourceUrl) },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("inshorts_source_button_${news.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MinimalPurpleLightContainer
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
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
        color = Color.White.copy(alpha = 0.08f)
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
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                )
            }
        }
    }
}
