import sys

with open("app/src/main/java/com/example/ui/components/TrendingTweetsRow.kt", "r") as f:
    content = f.read()

content = content.replace("TextPrimary", "MaterialTheme.colorScheme.onSurface")
content = content.replace("TextSecondary", "MaterialTheme.colorScheme.onSurfaceVariant")

with open("app/src/main/java/com/example/ui/components/TrendingTweetsRow.kt", "w") as f:
    f.write(content)
