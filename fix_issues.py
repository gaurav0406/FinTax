import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("DealsAndOffersTab(newsList)", "DealsAndOffersTab(allNewsList)")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

content = content.replace("creatorAvatarUrl = imageUrl,", 'creatorAvatarUrl = imageUrl ?: "",')

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
    f.write(content)
