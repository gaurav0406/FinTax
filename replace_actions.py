import re

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

pattern = r'(\s*// Actions Row.*?)(?=\s*} \s*val actionUrl = news\.financialActionUrl)'

replacement = r"""                    // Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Icons (Views, Bookmark, Share, Chat)
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp), verticalAlignment = Alignment.CenterVertically) {
                            
                            // Views
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Reads",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatSocialCount(news.readCount),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                )
                            }
                            
                            IconButton(
                                onClick = onToggleBookmark,
                                modifier = Modifier.size(36.dp).testTag("inshorts_bookmark_${news.id}")
                            ) {
                                Icon(
                                    imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (news.isBookmarked) MinimalPurpleLightContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            // Share
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 4.dp)
                                    .clickable {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, news.title)
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "⚡ ${news.title}\\n\\nKey Takeaway: ${news.summaryActionableTakeaway}\\n\\nRead 60-sec update: ${news.sourceUrl}"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
                                    }
                                    .testTag("inshorts_share_${news.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Article",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatSocialCount(news.shareCount),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                )
                            }
                            
                            if (onOpenComments != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .padding(horizontal = 4.dp)
                                        .clickable { onOpenComments() }
                                        .testTag("inshorts_comments_${news.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Comments",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatSocialCount(news.commentCount),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            }
                        }"""

match = re.search(pattern, text, re.DOTALL)
if match:
    print("Found! Replacing...")
    text = text.replace(match.group(1), replacement)
    with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
        f.write(text)
else:
    print("Could not find pattern.")
