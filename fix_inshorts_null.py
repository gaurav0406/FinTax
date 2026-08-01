import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Replace fallbackImage logic
new_fallback = """    val imageUrlToDisplay = news.imageUrl
    
    val fallbackColors = listOf(
        Color(0xFF2C3E50), Color(0xFF34495E), Color(0xFF1ABC9C), 
        Color(0xFF16A085), Color(0xFF27AE60), Color(0xFF2980B9),
        Color(0xFF8E44AD), Color(0xFF2C3E50), Color(0xFFE67E22), Color(0xFFD35400)
    )
    val bgColor = fallbackColors[news.id % fallbackColors.size]
"""

content = re.sub(
    r'val fallbackImage = when \(news\.category\) \{[\s\S]*?\}\n    val imageUrlToDisplay = news\.imageUrl \?: fallbackImage',
    new_fallback,
    content
)

# Replace AsyncImage with conditional AsyncImage
image_block_old = """            if (imageUrlToDisplay.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrlToDisplay)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }"""
            
image_block_new = """            if (!imageUrlToDisplay.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrlToDisplay)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(bgColor))
            }"""
content = content.replace(image_block_old, image_block_new)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
