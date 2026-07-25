import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

old_summary = """            // Summary Breakdown
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
                    color = TextPrimary
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )"""

new_summary = """            // Summary Breakdown
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
                text = news.summaryWhatHappened,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "WHO IS IMPACTED",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MinimalPurpleDark
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.summaryWhoImpacted,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "HOW YOU ARE IMPACTED",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MinimalPurpleDark
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.summaryText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "WHAT ARE THE NEXT STEPS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MinimalPurpleDark
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.summaryActionableTakeaway,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            )"""

content = content.replace(old_summary, new_summary)
with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
