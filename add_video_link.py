import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

target = """                        IconButton(
                            onClick = onCommentsClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }"""

replacement = target + """
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { /* Will open sourceUrl if passed, but onOpenDetails handles it */ onOpenDetails() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    contentPadding = PaddingValues(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = MinimalPurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Watch Video Link",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }"""

if target in text:
    print("Found target! Replacing...")
    text = text.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
        f.write(text)
else:
    print("Target not found.")

