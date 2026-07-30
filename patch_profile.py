import sys

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

content = content.replace("TextPrimary", "MaterialTheme.colorScheme.onSurface")
content = content.replace("TextSecondary", "MaterialTheme.colorScheme.onSurfaceVariant")
content = content.replace("Color.LightGray", "Color.Gray.copy(alpha = 0.5f)")

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
