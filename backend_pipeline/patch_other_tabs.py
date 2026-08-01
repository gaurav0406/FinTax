import re

with open("app/src/main/java/com/example/ui/components/SearchDiscoveryView.kt", "r") as f:
    content = f.read()
content = content.replace('"Mutual Funds", "Credit Cards", "EV Market"', '"Wealth 101", "Card Hacks & Perks", "Tech & AI"')
with open("app/src/main/java/com/example/ui/components/SearchDiscoveryView.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()
content = content.replace('listOf("All Deals", "Credit Cards", "Investments", "Bank & FDs")', 'listOf("All Deals", "Card Hacks & Perks", "Wealth 101", "Financial Markets")')
with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

# I will replace the manual `when` mapping in VideoEngagementTab to use the new categories
old_video_when = """    val videoData = when (news.category) {
        "Credit Cards" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video}",
            title = "Credit Card Hacks: 5 Hidden Benefits",
            presenterName = "Neha Sharma",
            tags = listOf("#CreditCards", "#FinanceHacks", "#Rewards")
        )
        "ITR & Tax" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video_2}",
            title = "New Tax Regime vs Old Tax Regime",
            presenterName = "CA Ramesh Kumar",
            tags = listOf("#TaxFiling", "#IncomeTax", "#SaveTax")
        )
        "Markets & Mutual Funds" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video_3}",
            title = "Top 3 SIPs for 2026 Growth",
            presenterName = "Priya Mehta",
            tags = listOf("#MutualFunds", "#StockMarket", "#Investing")
        )
        else -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video}",
            title = "Daily Financial News Briefing",
            presenterName = "FinTax Team",
            tags = listOf("#News", "#Finance", "#DailyUpdate")
        )
    }"""
    
new_video_when = """    val videoData = when (news.category) {
        "Card Hacks & Perks" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video}",
            title = "Credit Card Hacks: 5 Hidden Benefits",
            presenterName = "Neha Sharma",
            tags = listOf("#CreditCards", "#FinanceHacks", "#Rewards")
        )
        "Wealth 101" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video_2}",
            title = "New Tax Regime vs Old Tax Regime",
            presenterName = "CA Ramesh Kumar",
            tags = listOf("#TaxFiling", "#IncomeTax", "#SaveTax")
        )
        "Financial Markets" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video_3}",
            title = "Top 3 SIPs for 2026 Growth",
            presenterName = "Priya Mehta",
            tags = listOf("#MutualFunds", "#StockMarket", "#Investing")
        )
        "Tech & AI" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video}",
            title = "AI in Finance: What You Need to Know",
            presenterName = "Ravi Singh",
            tags = listOf("#AI", "#Tech", "#FutureFinance")
        )
        "Startup & Capital" -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video_2}",
            title = "Startup Funding Winter is Over",
            presenterName = "FinTax Team",
            tags = listOf("#Startups", "#Funding", "#VentureCapital")
        )
        else -> VideoNewsMetaData(
            videoUrl = "android.resource://com.example/${R.raw.sample_vertical_video}",
            title = "Daily Financial News Briefing",
            presenterName = "FinTax Team",
            tags = listOf("#News", "#Finance", "#DailyUpdate")
        )
    }"""
content = content.replace(old_video_when, new_video_when)
with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
    f.write(content)
