import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

pattern = r'private fun getVideoMetaData\(category: String, newsId: Int\): VideoNewsMetaData \{.*?else -> VideoNewsMetaData\([\s\S]*?creatorName = "FinTax Video Desk",[\s\S]*?\n        \)\n    \}'

new_func = """private fun getVideoMetaData(category: String, newsId: Int, sourceName: String? = null, imageUrl: String? = null): VideoNewsMetaData {
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
"""

text = re.sub(pattern, new_func, text, flags=re.DOTALL)
text = text.replace("val metaData = remember(news.category, news.id) { getVideoMetaData(news.category, news.id) }", "val metaData = remember(news.category, news.id) { getVideoMetaData(news.category, news.id, news.sourceName, news.imageUrl) }")

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
    f.write(text)

