package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.view.ViewGroup
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.NewsViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.*

data class VideoNewsMetaData(
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatarUrl: String,
    val videoDuration: String,
    val initialLikes: Int,
    val tags: List<String>
)

private fun getVideoMetaData(category: String, newsId: Int, sourceName: String? = null, imageUrl: String? = null): VideoNewsMetaData {
    if (category == "Video Shorts" || sourceName != "Indian Financial Feed") {
        return VideoNewsMetaData(
            creatorName = sourceName ?: "YouTube Creator",
            creatorHandle = "@${sourceName?.replace(" ", "") ?: "creator"}",
            creatorAvatarUrl = imageUrl ?: "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3",
            videoDuration = "0:60",
            initialLikes = 1500,
            tags = listOf("#Finance", "#Shorts")
        )
    }
    return when (category) {
        "Credit Cards" -> VideoNewsMetaData(
            creatorName = "Credit Hacks 60s",
            creatorHandle = "@credithacks_in",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80",
            videoDuration = "0:45",
            initialLikes = 1240 + (newsId * 37) % 800,
            tags = listOf("#CreditCards", "#Cashback", "#CardDeals")
        )
        "ITR & Tax" -> VideoNewsMetaData(
            creatorName = "Tax Wise Desk",
            creatorHandle = "@taxwise_official",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80",
            videoDuration = "0:52",
            initialLikes = 2150 + (newsId * 41) % 1200,
            tags = listOf("#TaxHacks", "#ITR2026", "#TaxExemption")
        )
        "Loans & FDs" -> VideoNewsMetaData(
            creatorName = "High Yield 60s",
            creatorHandle = "@smartbanking_in",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80",
            videoDuration = "0:40",
            initialLikes = 980 + (newsId * 29) % 600,
            tags = listOf("#FDInterest", "#HomeLoan", "#EMIOptimizer")
        )
        "Markets & Mutual Funds" -> VideoNewsMetaData(
            creatorName = "Stock Market Pulse",
            creatorHandle = "@marketreels_in",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&q=80",
            videoDuration = "1:00",
            initialLikes = 3400 + (newsId * 53) % 2000,
            tags = listOf("#Nifty50", "#SIPInvesting", "#MarketReel")
        )
        "RBI & Policy" -> VideoNewsMetaData(
            creatorName = "Monetary Desk",
            creatorHandle = "@rbi_bulletin",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=200&q=80",
            videoDuration = "0:35",
            initialLikes = 1890 + (newsId * 19) % 900,
            tags = listOf("#RBIPolicy", "#RepoRate", "#DigitalRupee")
        )
        else -> VideoNewsMetaData(
            creatorName = "FinTax Video Desk",
            creatorHandle = "@fintax_reels",
            creatorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80",
            videoDuration = "1:00",
            initialLikes = 1200,
            tags = listOf("#FinanceReels", "#Updates")
        )
    }
}


@Composable
fun VideoEngagementTab(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val newsList by viewModel.newsList.collectAsState()
    val bookmarkedList by viewModel.bookmarkedNews.collectAsState()
    val playbackState by viewModel.audioSpeechManager.playbackState.collectAsState()

    var selectedVideoCategory by remember { mutableStateOf("All") }
    var viewDisplayMode by remember { mutableStateOf(0) } // 0 = Full Screen Reels, 1 = Video Grid Feed
    var selectedNewsForComments by remember { mutableStateOf<FinancialNewsEntity?>(null) }
    var newsDetailDialogItem by remember { mutableStateOf<FinancialNewsEntity?>(null) }

    val videoCategories = listOf(
        "All", "60s Shorts", "Tax Hacks", "Market Reels", "AI & Tech", "FinTech", "Startups", "Personal Finance"
    )

    val videoNewsList = newsList.filter { it.sourceUrl.contains("youtube.com") }
    val filteredVideoNews = remember(videoNewsList, selectedVideoCategory) {
        if (selectedVideoCategory == "All") {
            videoNewsList
        } else {
            videoNewsList.filter { it.category.equals(selectedVideoCategory, ignoreCase = true) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Navigation & Mode Selector Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = MinimalPurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "FinTax Video Shorts",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MinimalPurplePrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "60s REELS",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        color = MinimalPurpleLightContainer
                                    )
                                )
                            }
                        }

                        // Toggle between Reel Mode & Grid Feed Mode
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(2.dp)
                        ) {
                            IconButton(
                                onClick = { viewDisplayMode = 0 },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (viewDisplayMode == 0) MinimalPurplePrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .testTag("video_mode_reels")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewStream,
                                    contentDescription = "Vertical Reels Mode",
                                    tint = if (viewDisplayMode == 0) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewDisplayMode = 1 },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (viewDisplayMode == 1) MinimalPurplePrimary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .testTag("video_mode_grid")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "Video Grid Feed",
                                    tint = if (viewDisplayMode == 1) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Category Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(videoCategories) { category ->
                            val isSelected = category == selectedVideoCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedVideoCategory = category },
                                label = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MinimalPurplePrimary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = Color.Transparent,
                                    selectedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag("video_category_$category")
                            )
                        }
                    }
                }
            }

            // Main Content Area
            if (filteredVideoNews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No videos available in $selectedVideoCategory",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (viewDisplayMode == 0) {
                // Full Screen Vertical Video Reels Pager
                val pagerState = rememberPagerState(pageCount = { filteredVideoNews.size })

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("video_vertical_pager")
                ) { page ->
                    val newsItem = filteredVideoNews[page]
                    val isBookmarked = bookmarkedList.any { bookmarked -> bookmarked.id == newsItem.id }
                    val isAudioPlaying = playbackState.isPlaying && playbackState.activeNewsId == newsItem.id

                    VideoReelItem(
                        news = newsItem,
                        isBookmarked = isBookmarked,
                        isAudioPlaying = isAudioPlaying,
                        onToggleBookmark = { viewModel.toggleBookmark(newsItem) },
                        onToggleAudio = {
                            if (isAudioPlaying) {
                                viewModel.audioSpeechManager.pauseAudio()
                            } else {
                                viewModel.playAudio(newsItem)
                            }
                        },
                        onOpenComments = { selectedNewsForComments = newsItem },
                        onOpenDetails = { newsDetailDialogItem = newsItem }
                    )
                }
            } else {
                // Video Grid / Cards Feed Mode
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVideoNews, key = { item -> item.id }) { newsItem ->
                        val isBookmarked = bookmarkedList.any { bookmarked -> bookmarked.id == newsItem.id }
                        val isAudioPlaying = playbackState.isPlaying && playbackState.activeNewsId == newsItem.id

                        VideoGridCard(
                            news = newsItem,
                            isBookmarked = isBookmarked,
                            isAudioPlaying = isAudioPlaying,
                            onPlayClick = { viewModel.playAudio(newsItem) },
                            onBookmarkClick = { viewModel.toggleBookmark(newsItem) },
                            onCommentsClick = { selectedNewsForComments = newsItem },
                            onOpenDetails = { newsDetailDialogItem = newsItem }
                        )
                    }
                }
            }
        }

        // Comments Sheet Dialog overlay
        selectedNewsForComments?.let { news ->
            CommentSheetDialog(
                news = news,
                viewModel = viewModel,
                onDismiss = { selectedNewsForComments = null }
            )
        }

        // Full Story Detail Dialog overlay
        newsDetailDialogItem?.let { news ->
            VideoStoryDetailDialog(
                news = news,
                onDismiss = { newsDetailDialogItem = null },
                onPlayAudio = { viewModel.playAudio(news) }
            )
        }
    }
}

@Composable
fun VideoReelItem(
    news: FinancialNewsEntity,
    isBookmarked: Boolean,
    isAudioPlaying: Boolean,
    onToggleBookmark: () -> Unit,
    onToggleAudio: () -> Unit,
    onOpenComments: () -> Unit,
    onOpenDetails: () -> Unit
) {
    val context = LocalContext.current
    val metaData = remember(news.category, news.id) { getVideoMetaData(news.category, news.id, news.sourceName, news.imageUrl) }
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(metaData.initialLikes) }
    var showHeartPulse by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Simulated Reel playback progress animation
    val progress = remember { Animatable(0f) }
    LaunchedEffect(news.id, isPaused) {
        if (!isPaused) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 45000, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(news.id) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!isLiked) {
                            isLiked = true
                            likeCount += 1
                        }
                        showHeartPulse = true
                    },
                    onTap = {
                        isPaused = !isPaused
                    }
                )
            }
    ) {
        // Video Cover / Thumbnail Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(news.imageUrl ?: "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80")
                .crossfade(true)
                .build(),
            contentDescription = news.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient scrim overlays for readibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Floating Play/Pause Indicator when user taps screen
        if (isPaused) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Heart Pulse on Double Tap
        LaunchedEffect(showHeartPulse) {
            if (showHeartPulse) {
                delay(800)
                showHeartPulse = false
            }
        }
        if (showHeartPulse) {
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart Pulse",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(100.dp)
                        .scale(1.2f)
                )
            }
        }

        // Top Status Badge & Audio Narration Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category & Duration Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MinimalPurplePrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = news.category,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.Red, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = metaData.videoDuration,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Audio Voiceover TTS Button
            IconButton(
                onClick = onToggleAudio,
                modifier = Modifier
                    .background(
                        if (isAudioPlaying) MinimalPurplePrimary else Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .size(40.dp)
                    .testTag("video_audio_toggle_${news.id}")
            ) {
                Icon(
                    imageVector = if (isAudioPlaying) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Toggle Audio Narration",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right Floating Action Sidebar Stack
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        if (isLiked) likeCount += 1 else likeCount -= 1
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("video_like_button_${news.id}")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Video",
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$likeCount",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.sp
                    )
                )
            }

            // Comment Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onOpenComments,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("video_comment_button_${news.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${news.shareCount / 3 + 12}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.sp
                    )
                )
            }

            // Bookmark Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("video_bookmark_button_${news.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) MinimalPurplePrimary else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBookmarked) "Saved" else "Save",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 10.sp
                    )
                )
            }

            // Share Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, news.title)
                            putExtra(Intent.EXTRA_TEXT, "🎬 Watch this 60s Financial Reel: ${news.title}\n${news.sourceUrl}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Reel via"))
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .testTag("video_share_button_${news.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${news.shareCount}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Bottom Overlay Content Block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 72.dp, bottom = 24.dp)
        ) {
            // Creator Handle & Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                AsyncImage(
                    model = metaData.creatorAvatarUrl,
                    contentDescription = metaData.creatorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = metaData.creatorName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = MinimalPurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "${metaData.creatorHandle} • ${news.readCount} views",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Headline Title
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 21.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Actionable Takeaway Highlight Pill
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MinimalPurplePrimary.copy(alpha = 0.5f))
            ) {
                Text(
                    text = news.summaryActionableTakeaway,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hashtags Row & "Read Full Story" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    metaData.tags.take(2).forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MinimalPurpleLightContainer,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Button(
                    onClick = onOpenDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Full Story",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Bottom Animated Progress Bar Scrubber
        LinearProgressIndicator(
            progress = progress.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter),
            color = MinimalPurplePrimary,
            trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
        )
    }
}

@Composable
fun VideoGridCard(
    news: FinancialNewsEntity,
    isBookmarked: Boolean,
    isAudioPlaying: Boolean,
    onPlayClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play Video",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = news.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBookmarkClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onCommentsClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onOpenDetails, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Open",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoStoryDetailDialog(
    news: FinancialNewsEntity,
    onDismiss: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val videoId = news.sourceUrl.substringAfter("v=").substringBefore("&")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = news.title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (videoId.isNotBlank() && news.sourceUrl.contains("youtube.com")) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                settings.javaScriptEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                webChromeClient = WebChromeClient()
                                webViewClient = WebViewClient()
                                loadUrl("https://www.youtube.com/embed/$videoId?autoplay=1")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Text("Video unavailable", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = news.summaryText, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = news.sourceName, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

