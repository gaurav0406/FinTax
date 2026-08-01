import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

spotlight_block = """                    if (selectedCategory == "Credit Cards" || selectedCategory == "All") {
                        item {
                            com.example.ui.components.DailyCreditCardSpotlightCard(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }"""

content = content.replace(spotlight_block, "")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
