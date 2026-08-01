import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

daily_digest_old = """                    if (dailyDigestList.isNotEmpty()) {
                        item {
                            com.example.ui.components.DailyDigestCard(
                                newsList = dailyDigestList,
                                allNewsList = if (allNewsList.isNotEmpty()) allNewsList else newsList,
                                onCategoryClick = onSelectCategory
                            )
                        }
                    }"""
daily_digest_new = """                    if (dailyDigestList.isNotEmpty()) {
                        item {
                            com.example.ui.components.SectionHeader("Daily Digest")
                        }
                        item {
                            com.example.ui.components.DailyDigestCard(
                                newsList = dailyDigestList,
                                allNewsList = if (allNewsList.isNotEmpty()) allNewsList else newsList,
                                onCategoryClick = onSelectCategory
                            )
                        }
                    }"""

content = content.replace(daily_digest_old, daily_digest_new)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

