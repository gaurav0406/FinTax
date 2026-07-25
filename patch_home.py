import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

old_block = """                items(newsList, key = { it.id }) { item ->
                    NewsItemCard(
                        news = item,
                        isPlaying = isPlaying && playingNewsId == item.id,
                        onPlayAudio = { onPlayAudio(item) },
                        onToggleBookmark = { onToggleBookmark(item) },
                        onOpenComments = if (onOpenComments != null) { { onOpenComments(item) } } else null,
                        autoPlayAudio = autoPlayAudio
                    )
                }
            }"""

new_block = """                items(newsList, key = { it.id }) { item ->
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
            }"""

content = content.replace(old_block, new_block)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
