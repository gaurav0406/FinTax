import sys

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

# Replace hardcoded Color.White with MaterialTheme.colorScheme.surface or background
content = content.replace(".background(androidx.compose.ui.graphics.Color.White)", ".background(MaterialTheme.colorScheme.background)")
content = content.replace("colors = CardDefaults.cardColors(containerColor = Color.White)", "colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)")
content = content.replace("color = if (isSelected) MinimalPurpleLightContainer else Color.White", "color = if (isSelected) MinimalPurpleLightContainer else MaterialTheme.colorScheme.surface")

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
