import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# Revert StandardCardListView parameter
content = content.replace("StandardCardListView(\n                            allNewsList = filteredNewsList,", "StandardCardListView(\n                            newsList = filteredNewsList,")
content = content.replace("StandardCardListView(\n                            allNewsList", "StandardCardListView(\n                            newsList")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

