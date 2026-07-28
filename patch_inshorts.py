import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Remove nextCategory computation
content = re.sub(r'val nextCategory = remember\(selectedCategory, categories\) \{.*?\n    \}\n', '', content, flags=re.DOTALL)

# Revert LaunchedEffect for autoSwipe
old_effect = """
    // Auto-advance loop & smooth transition to next category at the end of 5 cards
    LaunchedEffect(pagerState.currentPage, autoSwipeEnabled, swipeIntervalMs, selectedCategory) {
        if (pagerState.currentPage == interleavedSlides.size - 1 && nextCategory != null) {
            // Reached last slide of current category — auto-advance to next category
            kotlinx.coroutines.delay(6000)
            if (autoSwipeEnabled) {
                onSelectCategory(nextCategory)
            }
        } else if (autoSwipeEnabled && pagerState.currentPage < interleavedSlides.size - 1) {
            timeRemainingMs = swipeIntervalMs
            while (timeRemainingMs > 0) {
                kotlinx.coroutines.delay(1000)
                timeRemainingMs -= 1000
            }
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }
"""

new_effect = """
    LaunchedEffect(pagerState.currentPage, autoSwipeEnabled, swipeIntervalMs, selectedCategory) {
        if (autoSwipeEnabled && pagerState.currentPage < interleavedSlides.size - 1) {
            timeRemainingMs = swipeIntervalMs
            while (timeRemainingMs > 0) {
                kotlinx.coroutines.delay(1000)
                timeRemainingMs -= 1000
            }
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }
"""

# Replace LaunchedEffect
content = re.sub(r'LaunchedEffect\(pagerState\.currentPage, autoSwipeEnabled, swipeIntervalMs, selectedCategory\) \{.*?\}\n    \}', new_effect.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)

