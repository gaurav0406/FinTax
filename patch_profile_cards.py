import sys

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)",
    "elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),\n            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))"
)

# For categories, verify if we need to update category lines
content = content.replace("border = androidx.compose.foundation.BorderStroke(\n                                    width = 1.dp,\n                                    color = if (isSelected) MinimalPurpleDark else Color.Gray.copy(alpha = 0.5f),",
                          "border = androidx.compose.foundation.BorderStroke(\n                                    width = 1.dp,\n                                    color = if (isSelected) MinimalPurplePrimary else Color.Gray.copy(alpha = 0.5f),")

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
