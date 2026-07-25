import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# Replace Expand/Collapse toggle and 3 Bullet Points Breakdown with a fixed summary 3-4 lines
start_str = "            // Expand/Collapse Toggle"
end_str = "            Spacer(modifier = Modifier.height(16.dp))"

if start_str in content and end_str in content:
    pre = content[:content.find(start_str)]
    post = content[content.find(end_str):]
    
    new_summary_block = """            // Summary Breakdown
            Text(
                text = "SUMMARY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MinimalPurpleDark
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = news.summaryText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
"""
    content = pre + new_summary_block + post

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
