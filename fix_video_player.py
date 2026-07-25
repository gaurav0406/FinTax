import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

# Make sure we have AndroidView imported
if "import androidx.compose.ui.viewinterop.AndroidView" not in text:
    text = text.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.viewinterop.AndroidView\nimport android.webkit.WebView\nimport android.webkit.WebViewClient\nimport android.webkit.WebChromeClient\nimport android.view.ViewGroup\nimport androidx.compose.foundation.layout.aspectRatio")

# Find the VideoGridCard and VideoReelItem to replace with WebView. Wait, maybe we just use WebView for VideoReelItem.

# The user wants "high-level page of the video rather than showing the unnecessary images... neat and clean, structured, icon-based".
# I'll update VideoGridCard to just use a WebView if it's tapped. Or perhaps a cleaner layout.
# If they click, we show a dialog or just inline? "Whenever a user clicks in, the video runs in the player of the app itself"
# I can modify `VideoStoryDetailDialog` to contain a WebView.

video_dialog_pattern = r'fun VideoStoryDetailDialog\([\s\S]*?\}'

new_dialog = """fun VideoStoryDetailDialog(
    news: FinancialNewsEntity,
    onDismiss: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val videoId = news.sourceUrl.substringAfter("v=").substringBefore("&")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = news.title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (videoId.isNotBlank() && news.sourceUrl.contains("youtube.com")) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                settings.javaScriptEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                webChromeClient = WebChromeClient()
                                webViewClient = WebViewClient()
                                loadUrl("https://www.youtube.com/embed/$videoId?autoplay=1")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Text("Video unavailable", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = news.summaryText, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = news.sourceName, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}"""

text = re.sub(video_dialog_pattern, new_dialog, text, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
    f.write(text)

