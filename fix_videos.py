import re

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

# I see it takes `imageUrl: String? = null`. I should just use `imageUrl ?: "..."`
content = re.sub(
    r'creatorAvatarUrl = "https://images.unsplash.com/photo[^"]+"',
    r'creatorAvatarUrl = imageUrl ?: "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3"',
    content
)

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
    f.write(content)
