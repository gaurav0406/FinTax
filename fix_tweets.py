import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

tweets_block = """                    item {
                    com.example.ui.components.TrendingTweetsRow()
                }"""

content = content.replace(tweets_block, "")

# Some indentation variations just in case
tweets_block2 = """                    item {
                        com.example.ui.components.TrendingTweetsRow()
                    }"""
content = content.replace(tweets_block2, "")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
