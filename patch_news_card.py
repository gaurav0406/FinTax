import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# 1. Add date next to source name
import_text = "import androidx.compose.ui.unit.sp\nimport java.text.SimpleDateFormat\nimport java.util.Date\nimport java.util.Locale\nimport androidx.compose.ui.text.style.TextOverflow\n"
content = content.replace("import androidx.compose.ui.unit.sp", import_text)

# Add date display logic
source_name_block = """                    Text(
                        text = news.sourceName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )"""
date_display = """                    Text(
                        text = news.sourceName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• " + SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(news.publishedAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )"""
content = content.replace(source_name_block, date_display)

# 2. Replace Expand/Collapse toggle and 3 Bullet Points Breakdown with a fixed summary 3-4 lines
old_summary_block = """            // Expand/Collapse Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUMMARY BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand/Collapse",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 3 Bullet Points Breakdown
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MinimalBulletRow(
                        label = "👥 Who is impacted:",
                        text = news.summaryWhoImpacted
                    )
                    if (news.category in listOf("ITR & Tax", "Loans & FDs", "Credit Cards", "Tax")) {
                        MinimalBulletRow(
                            label = "💡 How you're impacted (Tangible/Intangible):",
                            text = news.summaryWhatHappened
                        )
                        MinimalBulletRow(
                            label = "🎯 Action to take (Risk & Benefits):",
                            text = news.summaryActionableTakeaway
                        )
                    }
                }
            }"""

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
            )"""

content = content.replace(old_summary_block, new_summary_block)

# 3. Top Stories as a Link? 
# The user wants "Digital stop stories news should be a link."
# Wait, maybe they mean "Top stories"? We have "Digital top stories"?
# In MainHomeScreen, there is no "Top stories". It's just the news feed. But we can ensure source link is prominent.
# Actually, the action buttons are "Take Action" and "Source Article". That's a link. 

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
