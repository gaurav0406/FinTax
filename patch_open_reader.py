import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

def replacement(match):
    return match.group(0).replace("viewModel.openArticleReader(news)", "try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(news.sourceUrl))) } catch (e: Exception) { e.printStackTrace() }")

content = re.sub(r'onOpenReader = \{ news -> viewModel\.openArticleReader\(news\) \}', replacement, content)

# Remove the AdaptiveArticleReaderScreen block entirely to clean up? Or just leave it if it's not opened anymore. I'll just leave it or comment it.
content = content.replace("val activeReaderNews = viewModel.activeReaderNews.collectAsState().value", "// val activeReaderNews = viewModel.activeReaderNews.collectAsState().value")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
