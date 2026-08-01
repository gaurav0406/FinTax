import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

daily_digest_old = """                    if (dailyDigestList.isNotEmpty()) {
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

daily_digest_new = """                    if (dailyDigestList.isNotEmpty() && selectedCategory == "All") {
                        item {
                            com.example.ui.components.SectionHeader("Daily Digest")
                        }
                        items(dailyDigestList, key = { "digest_" + it.id }) { item ->
                            com.example.ui.components.GoogleNewsSecondaryCard(
                                news = item,
                                isPlaying = isPlaying && playingNewsId == item.id,
                                onPlayAudio = { onPlayAudio(item) },
                                onToggleBookmark = { onToggleBookmark(item) },
                                onOpenComments = if (onOpenComments != null) { { onOpenComments(item) } } else null,
                                onOpenReader = { onOpenReader(item) },
                                autoPlayAudio = autoPlayAudio
                            )
                        }
                    }"""

content = content.replace(daily_digest_old, daily_digest_new)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

