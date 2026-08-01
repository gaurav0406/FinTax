import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

replacement = """                    if (selectedCategory == "Credit Cards" || selectedCategory == "All") {
                        item {
                            com.example.ui.components.DailyCreditCardSpotlightCard(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    
                    if (allNewsList.isNotEmpty() && selectedCategory == "All") {
                        item {
                            com.example.ui.components.GoogleNewsStyleCategoryFeed(
                                allNewsList = allNewsList,
                                onArticleClick = { onNavigateToInshorts(it.id) },
                                onCategoryClick = onSelectCategory
                            )
                        }
                    }
"""

content = content.replace("""                    if (selectedCategory == "Credit Cards" || selectedCategory == "All") {
                        item {
                            com.example.ui.components.DailyCreditCardSpotlightCard(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }""", replacement)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
