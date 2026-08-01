import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# Replace DealsAndOffersTab() with DealsAndOffersTab(newsList)
content = content.replace("4 -> DealsAndOffersTab()", "4 -> DealsAndOffersTab(newsList)")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
