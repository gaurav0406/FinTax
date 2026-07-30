import sys

with open("app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt", "r") as f:
    content = f.read()

content = content.replace("TextPrimary", "MaterialTheme.colorScheme.onSurface")
content = content.replace("TextSecondary", "MaterialTheme.colorScheme.onSurfaceVariant")
content = content.replace("focusedContainerColor = Color.White", "focusedContainerColor = MaterialTheme.colorScheme.surface")
content = content.replace("unfocusedContainerColor = Color.White", "unfocusedContainerColor = MaterialTheme.colorScheme.surface")
content = content.replace("focusedLabelColor = Color.Black", "focusedLabelColor = MaterialTheme.colorScheme.primary")
content = content.replace("unfocusedLabelColor = Color.Black", "unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant")
content = content.replace("Color.Black.copy(alpha = 0.08f)", "MaterialTheme.colorScheme.outlineVariant")
content = content.replace("Color.Black.copy(alpha = 0.1f)", "MaterialTheme.colorScheme.outlineVariant")
content = content.replace("color = if (isSelected) MinimalPurplePrimary else Color.White", "color = if (isSelected) MinimalPurplePrimary else MaterialTheme.colorScheme.surface")
content = content.replace("color = if (isSelected) Color.White else MinimalPurpleDark", "color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface")

with open("app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt", "w") as f:
    f.write(content)
