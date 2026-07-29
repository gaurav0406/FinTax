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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.HorizontalDivider
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.data.UserProfileEntity
import com.example.ui.components.CommentSheetDialog
import com.example.ui.components.CommunityDiscussionsTab
import com.example.ui.components.ProfileSetupScreen
import com.example.ui.components.DealsAndOffersTab
import com.example.ui.components.VideoEngagementTab
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.platform.LocalUriHandler
import androidx.activity.compose.BackHandler
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton

enum class ActiveDrawerDialog {
    NONE, SETTINGS, NOTIFICATIONS, ABOUT, HELP
}

val CATEGORIES = listOf(
    "All",
    "Financial News",
    "Credit Cards",
    "Mutual Funds",
    "Sports",
    "Cars & EVs",
    "Education",
    "Crypto",
    "Technology"
)

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
                    onLoginSuccess = { name, city ->
                        val updatedProfile = userProfileState!!.copy(
                            isLoggedIn = true, 
                            hasLoggedOut = false,
                            userName = name ?: userProfileState!!.userName,
                            city = city ?: userProfileState!!.city
                        )
                        viewModel.saveUserProfile(updatedProfile)
                    }
                )
            }
            return
        }
        
        if (!userProfileState!!.isOnboarded) {
            com.example.ui.components.OnboardingScreen(
                initialName = userProfileState!!.userName,
                initialCity = userProfileState!!.city,
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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    var activeDialog by remember { mutableStateOf(ActiveDrawerDialog.NONE) }
    val privacyPolicyUrl = "https://example.com/privacy-policy"
    val termsOfServiceUrl = "https://example.com/terms-of-service"
    val activity = (LocalContext.current as? Activity)

    BackHandler(enabled = true) {
        when {
            drawerState.isOpen -> {
                scope.launch { drawerState.close() }
            }
            selectedNewsForComments != null -> {
                selectedNewsForComments = null
            }
            activeDialog != ActiveDrawerDialog.NONE -> {
                activeDialog = ActiveDrawerDialog.NONE
            }
            activeTab != 0 -> {
                viewModel.setActiveTab(0)
            }
            else -> {
                activity?.finish()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                if (userProfileState != null) {
                    Surface(
                        onClick = {
                            viewModel.setActiveTab(6)
                            scope.launch { drawerState.close() }
                        },
                        color = Color.Transparent,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Hello, ${userProfileState!!.userName}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Age: ${userProfileState!!.age} | ${userProfileState!!.city}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = activeTab == 6,
                    onClick = { 
                        viewModel.setActiveTab(6)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = activeDialog == ActiveDrawerDialog.SETTINGS,
                    onClick = { 
                        activeDialog = ActiveDrawerDialog.SETTINGS
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_settings")
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Notifications") },
                    selected = activeDialog == ActiveDrawerDialog.NOTIFICATIONS,
                    onClick = { 
                        activeDialog = ActiveDrawerDialog.NOTIFICATIONS
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_notifications")
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("About") },
                    selected = activeDialog == ActiveDrawerDialog.ABOUT,
                    onClick = { 
                        activeDialog = ActiveDrawerDialog.ABOUT
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_about")
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                    label = { Text("Help & Support") },
                    selected = activeDialog == ActiveDrawerDialog.HELP,
                    onClick = { 
                        activeDialog = ActiveDrawerDialog.HELP
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_help")
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Privacy Policy") },
                    selected = false,
                    onClick = { 
                        try { uriHandler.openUri(privacyPolicyUrl) } catch (_: Exception) {}
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_privacy")
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Terms of Service") },
                    selected = false,
                    onClick = { 
                        try { uriHandler.openUri(termsOfServiceUrl) } catch (_: Exception) {}
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_terms")
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { 
                        if (userProfileState != null) {
                            viewModel.saveUserProfile(userProfileState!!.copy(isLoggedIn = false, hasLoggedOut = true))
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.testTag("drawer_item_logout")
                )
            }
        },
    ) {
        Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color(0xFF0D0E12) else MinimalBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (activeTab != 0) {
                        IconButton(
                            onClick = { viewModel.setActiveTab(0) },
                            modifier = Modifier.testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Feed",
                                tint = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color.White else TextPrimary
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("top_bar_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color.White else TextPrimary
                            )
                        }
                    }
                },
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
                            val headerTitle = when (activeTab) {
                                0 -> "FinTax Inshorts"
                                1 -> "Community Forum"
                                2 -> "Saved Reads"
                                3 -> "Tax Calculator"
                                4 -> "Deals & Offers"
                                5 -> "Video Reels"
                                6 -> "User Profile"
                                else -> "FinTax Inshorts"
                            }
                            val headerSubtitle = when (activeTab) {
                                0 -> if (useInshortsViewMode) "60-Sec Personal Finance Digest" else "All Financial Stories"
                                1 -> "Discuss Tax, Stocks & Investing"
                                2 -> "Your Bookmarked Reads"
                                3 -> "Compare Old vs New Tax Regime"
                                4 -> "Exclusive Financial Offers"
                                5 -> "60-Sec Financial Video Shorts"
                                6 -> "Manage Personal Information"
                                else -> ""
                            }
                            Text(
                                text = headerTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color.White else TextPrimary
                                )
                            )
                            if (headerSubtitle.isNotEmpty()) {
                                Text(
                                    text = headerSubtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.sp,
                                        color = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) MinimalPurpleLightContainer else TextSecondary
                                    )
                                )
                            }
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
                            tint = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color.White else TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color(0xFF0D0E12) else MinimalBackground,
                    titleContentColor = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color.White else TextPrimary
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

                val isDarkTab = (useInshortsViewMode && activeTab == 0) || activeTab == 5

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = if (isDarkTab) Color(0xFF16171E) else MinimalSurfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { viewModel.setActiveTab(0) },
                        icon = { Icon(imageVector = Icons.Default.Newspaper, contentDescription = "Feed") },
                        label = { Text("Feed", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = if (isDarkTab) Color.White else MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (isDarkTab) Color.Gray else TextSecondary,
                            unselectedTextColor = if (isDarkTab) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_feed")
                    )

                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { viewModel.setActiveTab(1) },
                        icon = { Icon(imageVector = Icons.Default.Forum, contentDescription = "Community") },
                        label = { Text("Forum", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = if (isDarkTab) Color.White else MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (isDarkTab) Color.Gray else TextSecondary,
                            unselectedTextColor = if (isDarkTab) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_community")
                    )

                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { viewModel.setActiveTab(2) },
                        icon = { Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved") },
                        label = { Text("Saved", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = if (isDarkTab) Color.White else MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (isDarkTab) Color.Gray else TextSecondary,
                            unselectedTextColor = if (isDarkTab) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_saved")
                    )

                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { viewModel.setActiveTab(3) },
                        icon = { Icon(imageVector = Icons.Default.Calculate, contentDescription = "Tax Calc") },
                        label = { Text("Taxes", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = if (isDarkTab) Color.White else MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (isDarkTab) Color.Gray else TextSecondary,
                            unselectedTextColor = if (isDarkTab) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_tax_calc")
                    )

                    NavigationBarItem(
                        selected = activeTab == 4,
                        onClick = { viewModel.setActiveTab(4) },
                        icon = { Icon(imageVector = Icons.Default.LocalOffer, contentDescription = "Deals") },
                        label = { Text("Deals", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = if (isDarkTab) Color.White else MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (isDarkTab) Color.Gray else TextSecondary,
                            unselectedTextColor = if (isDarkTab) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_deals")
                    )

                    NavigationBarItem(
                        selected = activeTab == 5,
                        onClick = { viewModel.setActiveTab(5) },
                        icon = { Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Videos") },
                        label = { Text("Videos", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = if (isDarkTab) Color.White else MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = if (isDarkTab) Color.Gray else TextSecondary,
                            unselectedTextColor = if (isDarkTab) Color.Gray else TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_videos")
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
                            allNewsList = filteredNewsList,
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

                2 -> InshortsFeedView(
                    allNewsList = bookmarkedList,
                    categories = emptyList(),
                    selectedCategory = "All",
                    playingNewsId = playbackState.activeNewsId,
                    isPlaying = playbackState.isPlaying,
                    onSelectCategory = {},
                    onPlayAudio = { viewModel.playAudio(it) },
                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                    onOpenComments = { news -> selectedNewsForComments = news }
                )

                3 -> TaxCalculatorTab()

                4 -> DealsAndOffersTab()

                5 -> com.example.ui.components.VideoEngagementTab(viewModel = viewModel)

                6 -> ProfileSetupScreen(viewModel = viewModel)
            }

            selectedNewsForComments?.let { news ->
                CommentSheetDialog(
                    news = news,
                    viewModel = viewModel,
                    onDismiss = { selectedNewsForComments = null }
                )
            }

            when (activeDialog) {
                ActiveDrawerDialog.SETTINGS -> SettingsDialog(
                    userProfile = userProfileState,
                    onSaveProfile = { viewModel.saveUserProfile(it) },
                    onRefreshFeeds = { viewModel.refreshFeeds() },
                    onDismiss = { activeDialog = ActiveDrawerDialog.NONE }
                )
                ActiveDrawerDialog.NOTIFICATIONS -> NotificationsDialog(
                    onDismiss = { activeDialog = ActiveDrawerDialog.NONE }
                )
                ActiveDrawerDialog.ABOUT -> AboutDialog(
                    onDismiss = { activeDialog = ActiveDrawerDialog.NONE }
                )
                ActiveDrawerDialog.HELP -> HelpSupportDialog(
                    onDismiss = { activeDialog = ActiveDrawerDialog.NONE }
                )
                ActiveDrawerDialog.NONE -> {}
            }
        }
    }
    }
}

@Composable
private fun SettingsDialog(
    userProfile: UserProfileEntity?,
    onSaveProfile: (UserProfileEntity) -> Unit,
    onRefreshFeeds: () -> Unit,
    onDismiss: () -> Unit
) {
    var autoPlayAudio by remember { mutableStateOf(userProfile?.autoPlayAudio ?: false) }
    var dailyDigestEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MinimalPurplePrimary)
                Spacer(Modifier.width(8.dp))
                Text("App Settings", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-play Audio", style = MaterialTheme.typography.titleMedium)
                        Text("Automatically play narration on scrolling cards", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Switch(
                        checked = autoPlayAudio,
                        onCheckedChange = { 
                            autoPlayAudio = it
                            if (userProfile != null) {
                                onSaveProfile(userProfile.copy(autoPlayAudio = it))
                            }
                        }
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Daily Digest Alerts", style = MaterialTheme.typography.titleMedium)
                        Text("Receive 9:00 AM personal finance notification", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Switch(
                        checked = dailyDigestEnabled,
                        onCheckedChange = { dailyDigestEnabled = it }
                    )
                }

                HorizontalDivider()

                Surface(
                    onClick = {
                        onRefreshFeeds()
                        Toast.makeText(context, "Syncing latest feed from Supabase...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = MinimalPurpleLightContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = MinimalPurpleDark)
                        Spacer(Modifier.width(8.dp))
                        Text("Force Refresh Live Feeds", fontWeight = FontWeight.Bold, color = MinimalPurpleDark)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", fontWeight = FontWeight.Bold, color = MinimalPurplePrimary)
            }
        }
    )
}

@Composable
private fun NotificationsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var taxDeadlines by remember { mutableStateOf(true) }
    var dailyDigest by remember { mutableStateOf(true) }
    var marketAlerts by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = MinimalPurplePrimary)
                Spacer(Modifier.width(8.dp))
                Text("Notification Schedule", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    color = MinimalSecondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Retention Schedule", fontWeight = FontWeight.Bold, color = MinimalPurpleDark)
                        Text("• Daily Financial Digest: 9:00 AM IST", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        Text("• ITR & Tax Due Dates: Active", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily 9 AM Finance Summary", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = dailyDigest, onCheckedChange = { dailyDigest = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tax Filing Deadline Reminders", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = taxDeadlines, onCheckedChange = { taxDeadlines = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Breaking Market Alerts", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = marketAlerts, onCheckedChange = { marketAlerts = it })
                }

                Surface(
                    onClick = {
                        RetentionNotificationScheduler.scheduleDailyNotifications(context)
                        Toast.makeText(context, "Test notification scheduled for 9:00 AM!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = MinimalPurplePrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Trigger Test Notification", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold, color = MinimalPurplePrimary)
            }
        }
    )
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MinimalPurplePrimary)
                Spacer(Modifier.width(8.dp))
                Text("About FinTax Inshorts", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "FinTax Inshorts v1.0.0",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A smart 60-second financial news & tax digest application engineered for Indian taxpayers, investors, and professionals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                HorizontalDivider()
                Text(
                    text = "🚀 Key Features & Pipeline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MinimalPurpleDark
                )
                Text("• 60-sec AI Batch Summarization via Gemini 2.0 Flash", style = MaterialTheme.typography.bodySmall)
                Text("• Real-time Supabase Database Syncing", style = MaterialTheme.typography.bodySmall)
                Text("• Audio TTS Narration with 1.0x-2.0x playback", style = MaterialTheme.typography.bodySmall)
                Text("• Old vs New Income Tax Calculator FY 2025-26", style = MaterialTheme.typography.bodySmall)
                Text("• Community Discussions & Community Polls", style = MaterialTheme.typography.bodySmall)
                HorizontalDivider()
                Text(
                    text = "Disclaimer: News summaries are AI-assisted for quick scanning. Please consult a qualified Chartered Accountant (CA) or financial advisor before making official tax or investment decisions.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got It", fontWeight = FontWeight.Bold, color = MinimalPurplePrimary)
            }
        }
    )
}

@Composable
private fun HelpSupportDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MinimalPurplePrimary)
                Spacer(Modifier.width(8.dp))
                Text("Help & Support", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Frequently Asked Questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Column {
                    Text("Q: How are news summaries generated?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("A: Our automated python pipeline scrapes verified Indian financial RSS feeds, batches articles, and uses Gemini 2.0 Flash to extract structured key takeaways.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                Column {
                    Text("Q: How do I use the Tax Calculator?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("A: Navigate to the Taxes tab, enter your gross annual income, standard deduction, 80C investments, and compare Old vs New regime taxes instantly.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                Column {
                    Text("Q: How do audio summaries work?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("A: Tap the speaker icon on any news card to listen to the audio summary. Use the floating player bar at the bottom to adjust playback speed.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                HorizontalDivider()

                Surface(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@fintaxinshorts.app")
                            putExtra(Intent.EXTRA_SUBJECT, "FinTax Inshorts Support Request")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Email support at: support@fintaxinshorts.app", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = MinimalPurplePrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Contact Support Team", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontWeight = FontWeight.Bold, color = MinimalPurplePrimary)
            }
        }
    )
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
                item {
                    com.example.ui.components.TrendingTweetsRow()
                }
            }
        }
    }
}
