import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("TextPrimary", "MaterialTheme.colorScheme.onSurface")
content = content.replace("TextSecondary", "MaterialTheme.colorScheme.onSurfaceVariant")
content = content.replace("unfocusedContainerColor = Color.White", "unfocusedContainerColor = MaterialTheme.colorScheme.surface")
content = content.replace("focusedContainerColor = Color.White", "focusedContainerColor = MaterialTheme.colorScheme.surface")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
