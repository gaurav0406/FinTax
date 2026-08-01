import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("var useInshortsViewMode by remember { mutableStateOf(true) }", "var useInshortsViewMode by remember { mutableStateOf(false) }")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
