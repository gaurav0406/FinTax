import re

with open("app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt", "r") as f:
    content = f.read()

# Make TrendingDiscussionCard have MinimalSurfaceVariant container color
content = content.replace("colors = CardDefaults.cardColors(containerColor = Color.White),", "colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.MinimalSurfaceVariant),")

# For DailyQuizCard, add border
old_quiz_card = """    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MinimalSurfaceVariant)
    ) {"""
new_quiz_card = """    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.MinimalSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {"""
content = content.replace(old_quiz_card, new_quiz_card)

with open("app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt", "w") as f:
    f.write(content)
