package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FinancialNewsEntity
import com.example.ui.components.AiGeneratorTab
import com.example.ui.components.FloatingAudioPlayer
import com.example.ui.components.InAppWebViewDialog
import com.example.ui.components.InshortsFeedView
import com.example.ui.components.NewsItemCard
import com.example.ui.components.PythonPipelineTab
import com.example.ui.components.TaxCalculatorTab
import com.example.ui.theme.MinimalBackground
import com.example.ui.theme.MinimalBorder
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.MinimalSecondaryContainer
import com.example.ui.theme.MinimalSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.notifications.RetentionNotificationScheduler
import com.example.ui.components.StickyBottomBannerAd

import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalOffer
import com.example.data.UserProfileEntity
import com.example.ui.components.CommentSheetDialog
import com.example.ui.components.CommunityDiscussionsTab
import com.example.ui.components.ProfileSetupScreen
import com.example.ui.components.DealsAndOffersTab

val CATEGORIES = listOf("All", "Credit Cards", "ITR & Tax", "Loans & FDs", "Markets & Mutual Funds", "RBI & Policy", "Sports", "Cars & EV", "Education")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allNewsList by viewModel.newsList.collectAsState()
    val bookmarkedList by viewModel.bookmarkedNews.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val playbackState by viewModel.audioSpeechManager.playbackState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val userProfileState by viewModel.userProfile.collectAsState()

    var useInshortsViewMode by remember { mutableStateOf(true) }
    var selectedNewsForComments by remember { mutableStateOf<FinancialNewsEntity?>(null) }

    val userSelectedCategories = remember(userProfileState) {
        userProfileState?.selectedCategories?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    val orderedCategories = remember(userSelectedCategories) {
        if (userSelectedCategories.isEmpty()) {
            CATEGORIES
        } else {
            val baseCategories = CATEGORIES.filter { it != "All" }
            val selectedInBase = baseCategories.filter { cat ->
                userSelectedCategories.any { it.equals(cat, ignoreCase = true) }
            }
            val remainingInBase = baseCategories.filter { cat ->
                !userSelectedCategories.any { it.equals(cat, ignoreCase = true) }
            }
            listOf("All") + selectedInBase + remainingInBase
        }
    }

    // Filter and order feed list according to user profile interests if "All" is selected
    val filteredNewsList = remember(allNewsList, selectedCategory, userSelectedCategories) {
        if (selectedCategory != "All") {
            allNewsList.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        } else if (userSelectedCategories.isNotEmpty()) {
            val selectedArticles = userSelectedCategories.flatMap { userCat ->
                allNewsList.filter { it.category.equals(userCat, ignoreCase = true) }
            }.distinctBy { it.id }
            
            val remainingArticles = allNewsList.filter { news ->
                !userSelectedCategories.any { it.equals(news.category, ignoreCase = true) }
            }
            selectedArticles + remainingArticles
        } else {
            allNewsList
        }
    }

    val dailyDigestList = remember(allNewsList) {
        val now = System.currentTimeMillis()
        allNewsList
            .filter { now - it.publishedAt <= 24L * 60 * 60 * 1000 }
            .sortedByDescending { it.publishedAt }
            .take(5)
    }

    LaunchedEffect(Unit) {
        RetentionNotificationScheduler.scheduleDailyNotifications(context)
    }

    if (userProfileState != null) {
        if (!userProfileState!!.isLoggedIn) {
            if (userProfileState!!.hasLoggedOut) {
                com.example.ui.components.LogoutScreen(
                    newsList = allNewsList,
                    onLoginAgain = {
                        viewModel.saveUserProfile(userProfileState!!.copy(hasLoggedOut = false))
                    }
                )
            } else {
                com.example.ui.components.LoginScreen(
                    onLoginSuccess = {
                        viewModel.saveUserProfile(userProfileState!!.copy(isLoggedIn = true, hasLoggedOut = false))
                    }
                )
            }
            return
        }
        
        if (!userProfileState!!.isOnboarded) {
            com.example.ui.components.OnboardingScreen(
                onComplete = { name, age, city, mobile, categories ->
                    val profile = userProfileState!!.copy(
                        isOnboarded = true,
                        userName = name,
                        age = age,
                        city = city,
                        mobileNumber = mobile,
                        selectedCategories = categories.joinToString(",")
                    )
                    viewModel.saveUserProfile(profile)
                }
            )
            return
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (useInshortsViewMode && activeTab == 0) Color(0xFF0D0E12) else MinimalBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MinimalPurplePrimary,
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "FinTax Inshorts",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = if (useInshortsViewMode && activeTab == 0) Color.White else TextPrimary
                                )
                            )
                            Text(
                                text = "60-Sec Personal Finance Digest",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = if (useInshortsViewMode && activeTab == 0) MinimalPurpleLightContainer else TextSecondary
                                )
                            )
                        }
                    }
                },
                actions = {
                    if (activeTab == 0) {
                        IconButton(
                            onClick = { useInshortsViewMode = !useInshortsViewMode },
                            modifier = Modifier.testTag("toggle_inshorts_view_mode")
                        ) {
                            Icon(
                                imageVector = if (useInshortsViewMode) Icons.Default.ViewDay else Icons.Default.SwapVert,
                                contentDescription = "Toggle View Mode",
                                tint = if (useInshortsViewMode) Color.White else TextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.refreshFeeds() },
                        modifier = Modifier.testTag("refresh_feeds_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Feeds",
                            tint = if (useInshortsViewMode && activeTab == 0) Color.White else TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (useInshortsViewMode && activeTab == 0) Color(0xFF0D0E12) else MinimalBackground,
                    titleContentColor = if (useInshortsViewMode && activeTab == 0) Color.White else TextPrimary
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Floating audio bar if audio is active and not on Inshorts tab (where FAB handles it)
                if (!useInshortsViewMode || activeTab != 0) {
                    FloatingAudioPlayer(
                        playbackState = playbackState,
                        onPlayPauseToggle = {
                            if (playbackState.isPlaying) {
                                viewModel.audioSpeechManager.pauseAudio()
                            } else {
                                val activeId = playbackState.activeNewsId
                                val newsItem = filteredNewsList.find { it.id == activeId } ?: bookmarkedList.find { it.id == activeId }
                                if (newsItem != null) {
                                    viewModel.playAudio(newsItem)
                                }
                            }
                        },
                        onCycleRate = { viewModel.audioSpeechManager.cycleSpeechRate() },
                        onStop = { viewModel.audioSpeechManager.stopAudio() }
                    )
                }

                // Persistent Bottom Sticky Banner Ad on non-feed screens
                if (activeTab != 0) {
                    StickyBottomBannerAd()
                }

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = if (useInshortsViewMode && activeTab == 0) Color(0xFF16171E) else MinimalSurfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { viewModel.setActiveTab(0) },
                        icon = { Icon(imageVector = Icons.Default.Newspaper, contentDescription = "Feed") },
                        label = { Text("Feed", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (useInshortsViewMode && activeTab == 0) Color.Gray else TextSecondary,
                            unselectedTextColor = if (useInshortsViewMode && activeTab == 0) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_feed")
                    )

                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { viewModel.setActiveTab(1) },
                        icon = { Icon(imageVector = Icons.Default.Forum, contentDescription = "Community") },
                        label = { Text("Forum", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_community")
                    )

                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { viewModel.setActiveTab(2) },
                        icon = { Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved") },
                        label = { Text("Saved", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_saved")
                    )

                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { viewModel.setActiveTab(3) },
                        icon = { Icon(imageVector = Icons.Default.Calculate, contentDescription = "Tax Calc") },
                        label = { Text("Taxes", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_tax_calc")
                    )

                    NavigationBarItem(
                        selected = activeTab == 4,
                        onClick = { viewModel.setActiveTab(4) },
                        icon = { Icon(imageVector = Icons.Default.LocalOffer, contentDescription = "Deals") },
                        label = { Text("Deals", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_deals")
                    )

                    NavigationBarItem(
                        selected = activeTab == 5,
                        onClick = { viewModel.setActiveTab(5) },
                        icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> {
                    if (useInshortsViewMode) {
                        InshortsFeedView(
                            newsList = filteredNewsList,
                            dailyDigestList = dailyDigestList,
                            categories = orderedCategories,
                            selectedCategory = selectedCategory,
                            playingNewsId = playbackState.activeNewsId,
                            isPlaying = playbackState.isPlaying,
                            isRefreshing = isRefreshing,
                            onRefresh = { viewModel.refreshFeeds() },
                            onSelectCategory = { viewModel.setCategory(it) },
                            onPlayAudio = { viewModel.playAudio(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onOpenComments = { news -> selectedNewsForComments = news }
                        )
                    } else {
                        StandardCardListView(
                            newsList = filteredNewsList,
                            dailyDigestList = dailyDigestList,
                            categories = orderedCategories,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            playingNewsId = playbackState.activeNewsId,
                            isPlaying = playbackState.isPlaying,
                            onSelectCategory = { viewModel.setCategory(it) },
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onPlayAudio = { viewModel.playAudio(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onOpenComments = { news -> selectedNewsForComments = news },
                            autoPlayAudio = userProfileState?.autoPlayAudio == true
                        )
                    }
                }

                1 -> CommunityDiscussionsTab(viewModel = viewModel)

                2 -> StandardCardListView(
                    newsList = bookmarkedList,
                    categories = emptyList(),
                    selectedCategory = "All",
                    searchQuery = "",
                    playingNewsId = playbackState.activeNewsId,
                    isPlaying = playbackState.isPlaying,
                    onSelectCategory = {},
                    onSearchQueryChange = {},
                    onPlayAudio = { viewModel.playAudio(it) },
                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                    onOpenComments = { news -> selectedNewsForComments = news },
                    autoPlayAudio = userProfileState?.autoPlayAudio == true,
                    emptyMessage = "No saved articles yet! Tap the bookmark icon on any news card to save for offline reading."
                )

                3 -> TaxCalculatorTab()

                4 -> DealsAndOffersTab()

                5 -> ProfileSetupScreen(viewModel = viewModel)
            }

            selectedNewsForComments?.let { news ->
                CommentSheetDialog(
                    news = news,
                    viewModel = viewModel,
                    onDismiss = { selectedNewsForComments = null }
                )
            }
        }
    }
}

@Composable
private fun StandardCardListView(
    newsList: List<FinancialNewsEntity>,
    dailyDigestList: List<FinancialNewsEntity> = emptyList(),
    categories: List<String>,
    selectedCategory: String,
    searchQuery: String,
    playingNewsId: Int?,
    isPlaying: Boolean,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPlayAudio: (FinancialNewsEntity) -> Unit,
    onToggleBookmark: (FinancialNewsEntity) -> Unit,
    onOpenComments: ((FinancialNewsEntity) -> Unit)? = null,
    autoPlayAudio: Boolean = false,
    emptyMessage: String = "No articles found in this category."
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (categories.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MinimalBackground)
                    .padding(vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("search_news_input"),
                    placeholder = { Text("Search ITR, Repo Rate, Section 80C...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = MinimalBorder,
                        focusedBorderColor = MinimalPurplePrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Category Filter",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
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
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MinimalSecondaryContainer,
                                selectedLabelColor = MinimalPurpleDark,
                                containerColor = Color.Transparent,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MinimalBorder,
                                selectedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }
            }
        }

        if (newsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (dailyDigestList.isNotEmpty()) {
                    item {
                        com.example.ui.components.DailyDigestCard(
                            newsList = dailyDigestList,
                            allNewsList = newsList,
                            onCategoryClick = onSelectCategory
                        )
                    }
                }
                items(newsList, key = { it.id }) { item ->
                    NewsItemCard(
                        news = item,
                        isPlaying = isPlaying && playingNewsId == item.id,
                        onPlayAudio = { onPlayAudio(item) },
                        onToggleBookmark = { onToggleBookmark(item) },
                        onOpenComments = if (onOpenComments != null) { { onOpenComments(item) } } else null,
                        autoPlayAudio = autoPlayAudio
                    )
                }
            }
        }
    }
}
