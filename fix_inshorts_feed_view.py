import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Extract from fun InshortsFeedView to the closing brace before @Composable fun InshortsNewsCardItem
# Use regex to find InshortsNewsCardItem
match = re.search(r'(@Composable\s*fun InshortsNewsCardItem)', content)
if match:
    inshorts_news_card_item_idx = match.start()
    
    # We will replace everything from `fun InshortsFeedView` to `inshorts_news_card_item_idx`
    start_match = re.search(r'@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun InshortsFeedView', content)
    if start_match:
        start_idx = start_match.start()
        
        new_inshorts_feed_view = """@OptIn(ExperimentalMaterial3Api::class)
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
                else allNewsList.filter { it.category.equals(currentCat, ignoreCase = true) }
            }
            val displayNewsList = remember(catNewsList, currentCat) {
                if (currentCat != "All") catNewsList.take(5) else catNewsList
            }

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
                                selectedContainerColor = MinimalPurplePrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                                labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
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

"""
        content = content[:start_idx] + new_inshorts_feed_view + content[inshorts_news_card_item_idx:]
        with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as out:
            out.write(content)
