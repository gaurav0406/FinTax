import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

pattern = r'val categories = listOf\("All", "60s Shorts", "Tax Hacks", "Market Reels", "AI & Tech", "FinTech", "Startups", "Personal Finance"\)'
text = re.sub(pattern, 'val categories = listOf("All", "ITR & Tax", "Credit Cards", "Loans & FDs", "Markets & Mutual Funds", "RBI & Policy")', text)

pattern2 = r'val filteredVideoNews = remember\(newsList, selectedVideoCategory\) \{[\s\S]*?\n    \}'

new_filtered = """val videoNewsList = newsList.filter { it.sourceUrl.contains("youtube.com") }
    val filteredVideoNews = remember(videoNewsList, selectedVideoCategory) {
        if (selectedVideoCategory == "All") {
            videoNewsList
        } else {
            videoNewsList.filter { it.category.equals(selectedVideoCategory, ignoreCase = true) }
        }
    }"""

text = re.sub(pattern2, new_filtered, text)

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
    f.write(text)

