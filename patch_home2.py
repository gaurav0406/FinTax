import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("onArticleClick = { onNavigateToInshorts(it.id) }", "onArticleClick = { onOpenReader(it) }")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
