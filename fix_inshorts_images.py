import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# find fallback image logic and replace it with just news.imageUrl if possible, but keep fallback if null
content = re.sub(
    r'val fallbackImage = when \(news\.category\) \{[\s\S]*?\}',
    r'val fallbackImage = news.imageUrl ?: when (news.category) {\n        "Financial News" -> "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80"\n        "Credit Cards" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"\n        "Loans & FDs" -> "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80"\n        "Markets & Mutual Funds" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"\n        else -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80"\n    }',
    content
)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
