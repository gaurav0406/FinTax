import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

card_pattern = r'fun VideoGridCard\([\s\S]*?fun VideoStoryDetailDialog'

new_card = """fun VideoGridCard(
    news: FinancialNewsEntity,
    isBookmarked: Boolean,
    isAudioPlaying: Boolean,
    onPlayClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play Video",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = news.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBookmarkClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onCommentsClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onOpenDetails, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Open",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

fun VideoStoryDetailDialog"""

text = re.sub(card_pattern, new_card, text, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
    f.write(text)

