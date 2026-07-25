import re

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

# First, remove the "Metrics (Reads & Shares)" block entirely.
# It starts at `// Metrics (Reads & Shares)` and ends before `// Actions Row`
pattern = r'(\s*// Metrics \(Reads & Shares\).*?)(\s*Spacer\(modifier = Modifier\.height\(16\.dp\)\))'
match = re.search(pattern, text, re.DOTALL)
if match:
    print("Found metrics block to remove.")
    text = text.replace(match.group(1), '')

# Now replace the Actions Row with the new one.
target_actions = """                    // Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Icons (Bookmark, Share, Chat)
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                            IconButton(
                                onClick = onToggleBookmark,
                                modifier = Modifier.size(40.dp).testTag("inshorts_bookmark_${news.id}")
                            ) {
                                Icon(
                                    imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (news.isBookmarked) MinimalPurpleLightContainer else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, news.title)
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "⚡ ${news.title}\\n\\nKey Takeaway: ${news.summaryActionableTakeaway}\\n\\nRead 60-sec update: ${news.sourceUrl}"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
                                },
                                modifier = Modifier.size(40.dp).testTag("inshorts_share_${news.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Article",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            }
                            if (onOpenComments != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .padding(horizontal = 8.dp)
                                        .clickable { onOpenComments() }
                                        .testTag("inshorts_comments_${news.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Comments",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                        modifier = Modifier.size(24.dp)
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

replacement_actions = """                    // Actions Row
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
                                    modifier = Modifier.size(20.dp)
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
                                    modifier = Modifier.size(20.dp)
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
                                    modifier = Modifier.size(20.dp)
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
                                        modifier = Modifier.size(20.dp)
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

if target_actions in text:
    print("Found actions row to replace.")
    text = text.replace(target_actions, replacement_actions)
else:
    print("Actions row NOT FOUND!")
    with open('debug_text.txt', 'w') as dbg:
        dbg.write(text)

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
    f.write(text)

