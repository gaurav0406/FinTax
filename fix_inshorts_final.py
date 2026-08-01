import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Replace the block
bad_block = """    val fallbackImage = news.imageUrl ?: when (news.category) {
        "Financial News" -> "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80"
        "Credit Cards" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"
        "Loans & FDs" -> "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80"
        "Markets & Mutual Funds" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"
        else -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80"
    }
    val imageUrlToDisplay = news.imageUrl ?: fallbackImage"""

new_block = """    val imageUrlToDisplay = news.imageUrl
    
    val fallbackColors = listOf(
        Color(0xFF2C3E50), Color(0xFF34495E), Color(0xFF1ABC9C), 
        Color(0xFF16A085), Color(0xFF27AE60), Color(0xFF2980B9),
        Color(0xFF8E44AD), Color(0xFF2C3E50), Color(0xFFE67E22), Color(0xFFD35400)
    )
    val bgColor = fallbackColors[news.id % fallbackColors.size]"""

content = content.replace(bad_block, new_block)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
