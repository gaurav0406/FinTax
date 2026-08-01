import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

# Remove the duplicated lines
content = content.replace('    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }\n    var webViewTitleToOpen by remember { mutableStateOf("Offer Details") }\n    var selectedCategory by remember { mutableStateOf("All Deals") }\n\n    val categories = listOf("All Deals", "Credit Cards", "Investments", "Bank & FDs")', '    val categories = listOf("All Deals", "Credit Cards", "Investments", "Bank & FDs")')

# Change news.text to news.summaryText
content = content.replace("news.text ?: \"\"", "news.summaryText")

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
