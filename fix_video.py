import re

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

# Replace creator avatar fallback
content = re.sub(
    r'creatorAvatarUrl = imageUrl \?: "https://images\.unsplash\.com[^"]+"',
    r'creatorAvatarUrl = imageUrl',
    content
)

# Replace poster fallback
content = re.sub(
    r'\.data\(news\.imageUrl \?: "https://images\.unsplash\.com[^"]+"\)',
    r'.data(news.imageUrl)',
    content
)

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
    f.write(content)
