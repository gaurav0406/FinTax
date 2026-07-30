import sys

with open("app/src/main/java/com/example/ui/components/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Replace JobProfilePage colors
content = content.replace(
    "containerColor = if (isSelected) MinimalPurpleLightContainer else MaterialTheme.colorScheme.surface",
    "containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant"
)
content = content.replace(
    "if (isSelected) MinimalPurplePrimary else Color.Gray.copy(alpha = 0.5f)",
    "if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)"
)
content = content.replace(
    "color = if (isSelected) MinimalPurpleDark else MaterialTheme.colorScheme.onSurface",
    "color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant"
)
content = content.replace(
    "tint = MinimalPurplePrimary",
    "tint = MaterialTheme.colorScheme.primary"
)
content = content.replace(
    "tint = MinimalPurpleDark",
    "tint = MaterialTheme.colorScheme.onPrimaryContainer"
)
content = content.replace(
    "color = if (isSelected) MinimalPurpleLightContainer else MaterialTheme.colorScheme.surface",
    "color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant"
)

# Replace UserDetailsPage Button colors if any
content = content.replace(
    "colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)",
    "colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)"
)


with open("app/src/main/java/com/example/ui/components/OnboardingScreen.kt", "w") as f:
    f.write(content)
