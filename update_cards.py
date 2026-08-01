import re

with open("app/src/main/java/com/example/ui/components/GoogleNewsCards.kt", "r") as f:
    content = f.read()

hero_indicator = """                Spacer(modifier = Modifier.height(14.dp))
                
                // AI Insight Indicator
                if (news.summaryActionableTakeaway.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Insight", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Why it matters: ${news.summaryActionableTakeaway}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Footer (Time & Actions)"""

content = content.replace("                Spacer(modifier = Modifier.height(14.dp))\n\n                // Footer (Time & Actions)", hero_indicator)

secondary_indicator = """                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // AI Insight Indicator
                if (news.summaryActionableTakeaway.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Insight", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Impact: ${news.summaryActionableTakeaway}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row("""

content = content.replace("                }\n\n                Row(", secondary_indicator)


with open("app/src/main/java/com/example/ui/components/GoogleNewsCards.kt", "w") as f:
    f.write(content)
