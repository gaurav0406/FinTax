import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# Replace the block
block_to_remove = """            // val activeReaderNews = viewModel.activeReaderNews.collectAsState().value
            activeReaderNews?.let { news ->
                com.example.ui.components.AdaptiveArticleReaderScreen(
                    newsId = news.id,
                    initialNews = news,
                    viewModel = viewModel,
                    onBack = { viewModel.closeArticleReader() }
                )
            }"""

content = content.replace(block_to_remove, "")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
