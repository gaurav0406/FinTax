import sys

with open("app/src/main/java/com/example/ui/components/ValuePropositionBanner.kt", "r") as f:
    content = f.read()

content = content.replace("com.example.ui.theme.TextPrimary", "MaterialTheme.colorScheme.onSurface")
content = content.replace("com.example.ui.theme.TextSecondary", "MaterialTheme.colorScheme.onSurfaceVariant")

with open("app/src/main/java/com/example/ui/components/ValuePropositionBanner.kt", "w") as f:
    f.write(content)
