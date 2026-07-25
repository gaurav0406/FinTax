import re

with open("app/src/main/java/com/example/ui/components/DailyDigestCard.kt", "r") as f:
    content = f.read()

# Add border to DailyDigestCard Card
old_card = """    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MinimalPurpleLightContainer)
    ) {"""
new_card = """    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MinimalPurpleLightContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {"""
content = content.replace(old_card, new_card)

with open("app/src/main/java/com/example/ui/components/DailyDigestCard.kt", "w") as f:
    f.write(content)
