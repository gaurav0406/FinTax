import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# Make sure we import SectionHeader, GoogleNewsHeroCard, GoogleNewsSecondaryCard
if "import com.example.ui.components.SectionHeader" not in content:
    content = content.replace(
        "import com.example.ui.components.StandardCardListView", 
        "import com.example.ui.components.StandardCardListView\nimport com.example.ui.components.SectionHeader\nimport com.example.ui.components.GoogleNewsHeroCard\nimport com.example.ui.components.GoogleNewsSecondaryCard"
    )

old_items_block = """                    items(newsList, key = { it.id }) { item ->
                        NewsItemCard(
                            news = item,
                            isPlaying = isPlaying && playingNewsId == item.id,
                            onPlayAudio = { onPlayAudio(item) },
                            onToggleBookmark = { onToggleBookmark(item) },
                            onOpenComments = if (onOpenComments != null) { { onOpenComments(item) } } else null,
                            autoPlayAudio = autoPlayAudio,
                            onOpenReader = { onOpenReader(item) }
                        )
                    }"""

if old_items_block not in content:
    old_items_block = """                    items(newsList, key = { it.id }) { item ->
                        NewsItemCard(
                            news = item,
                            isPlaying = isPlaying && playingNewsId == item.id,
                            onPlayAudio = { onPlayAudio(item) },
                            onToggleBookmark = { onToggleBookmark(item) },
                            onOpenComments = if (onOpenComments != null) { { onOpenComments(item) } } else null,
                            onOpenReader = { onOpenReader(item) },
                            autoPlayAudio = autoPlayAudio
                        )
                    }"""

if old_items_block not in content:
    # try one more variation
    old_items_block = """                    items(newsList, key = { it.id }) { item ->
                        NewsItemCard(
                            news = item,
                            isPlaying = isPlaying && playingNewsId == item.id,
                            onPlayAudio = { onPlayAudio(item) },
                            onToggleBookmark = { onToggleBookmark(item) },
                            onOpenComments = if (onOpenComments != null) { { onOpenComments(item) } } else null,
                            autoPlayAudio = autoPlayAudio
                        )
                    }"""

new_items_block = """                    if (newsList.isNotEmpty()) {
                        item {
                            com.example.ui.components.SectionHeader("Top Stories")
                        }
                        
                        item {
                            com.example.ui.components.GoogleNewsHeroCard(
                                news = newsList.first(),
                                isPlaying = isPlaying && playingNewsId == newsList.first().id,
                                onPlayAudio = { onPlayAudio(newsList.first()) },
                                onToggleBookmark = { onToggleBookmark(newsList.first()) },
                                onOpenComments = if (onOpenComments != null) { { onOpenComments(newsList.first()) } } else null,
                                onOpenReader = { onOpenReader(newsList.first()) },
                                autoPlayAudio = autoPlayAudio
                            )
                        }
                        
                        if (newsList.size > 1) {
                            item {
                                com.example.ui.components.SectionHeader(if (selectedCategory == "All") "Market Trends" else "More in $selectedCategory")
                            }
                            
                            items(newsList.drop(1), key = { it.id }) { item ->
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
                        }
                    }"""

content = content.replace(old_items_block, new_items_block)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

