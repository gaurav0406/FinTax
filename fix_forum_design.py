import re

with open("app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt", "r") as f:
    content = f.read()

# Make TrendingDiscussionCard background light gray and have a subtle border
content = content.replace(
    "colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.MinimalSurfaceVariant),", 
    "colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF8F9FA)),"
)
content = content.replace(
    "border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),",
    "border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFE0E0E0)),"
)

with open("app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt", "w") as f:
    f.write(content)
