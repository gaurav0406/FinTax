import sys

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

content = content.replace("iconColor = MaterialTheme.colorScheme.secondary,", "iconColor = MaterialTheme.colorScheme.primary,")

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
