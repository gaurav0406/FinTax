import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Add necessary imports
imports = """
import androidx.compose.foundation.pager.HorizontalPager
"""
if "import androidx.compose.foundation.pager.HorizontalPager" not in content:
    content = content.replace("import androidx.compose.foundation.pager.VerticalPager", "import androidx.compose.foundation.pager.HorizontalPager\nimport androidx.compose.foundation.pager.VerticalPager")

# Change parameter from newsList to allNewsList
content = content.replace("fun InshortsFeedView(\n    newsList: List<FinancialNewsEntity>,", "fun InshortsFeedView(\n    allNewsList: List<FinancialNewsEntity>,")

# Find the start of the logic
# Replace the top part
logic_start = """
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

    HorizontalPager(
        state = horizontalPagerState,
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
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
"""

old_logic_pattern = r'if \(newsList\.isEmpty\(\)\) \{.*?when \(val slide = interleavedSlides\[page\]\) \{'

content = re.sub(old_logic_pattern, logic_start.strip() + "                when (val slide = interleavedSlides[vPage]) {", content, flags=re.DOTALL)

# Fix the variable references in the when block
content = content.replace("slideIndex = page", "slideIndex = vPage")
content = content.replace("pageIndex = page,", "pageIndex = vPage,")

# Now add closing braces for HorizontalPager
content = re.sub(r'(\s*\)\s*\}\s*)$', r'\1\n        }\n    }\n}', content)
# Wait, I shouldn't just append, I should replace the last closing braces of PullToRefreshBox properly.
# The original ended with `        }\n    }\n}` (PullToRefreshBox close, InshortsFeedView close). 
# I can just append `}` because `HorizontalPager` was added around `PullToRefreshBox`.

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)

