import re
with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

# Update background color
content = content.replace("colors = CardDefaults.cardColors(containerColor = Color.White),", "colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF8F9FA)),")

# Add border
if "border =" not in content:
    content = content.replace("elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)", "border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFE0E0E0)),\n        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)")
else:
    content = content.replace(
        "border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),",
        "border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFE0E0E0)),"
    )

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
