import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# Replace all MaterialTheme.colorScheme.onSurface with TextPrimary or MinimalPurpleDark appropriately
# Let's just use TextPrimary for onSurface and TextSecondary for onSurfaceVariant

# In Category Badge Surface color:
content = content.replace("color = MaterialTheme.colorScheme.onSurface,", "color = MinimalPurplePrimary,")

# For other onSurface / onSurfaceVariant
content = content.replace("MaterialTheme.colorScheme.onSurfaceVariant", "TextSecondary")
content = content.replace("MaterialTheme.colorScheme.onSurface", "TextPrimary")

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
