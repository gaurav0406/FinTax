import re

with open('app/src/main/java/com/example/ui/components/CommentSheetDialog.kt', 'r') as f:
    text = f.read()

replacement = """@Composable
fun CommentItemRow(
    comment: CommentEntity,
    isReply: Boolean = false,
    onUpvote: () -> Unit,
    onReply: () -> Unit
) {
    val dateStr = remember(comment.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(comment.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isReply) 16.dp else 0.dp)
    ) {
        if (isReply) {
            // Thread line / "Train line" for nested comments
            Box(
                modifier = Modifier
                    .padding(end = 12.dp, top = 8.dp)
                    .width(2.dp)
                    .height(40.dp)
                    .background(Color.LightGray)
            )
        }

        // Avatar circle
        Surface(
            modifier = Modifier.size(if (isReply) 28.dp else 36.dp),
            shape = CircleShape,
            color = if (isReply) MinimalPurpleLightContainer else MinimalPurplePrimary
        ) {"""

text = re.sub(r'@Composable\s*fun CommentItemRow\(.*?color = if \(isReply\) MinimalPurpleLightContainer else MinimalPurplePrimary\s*\) \{', replacement, text, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/components/CommentSheetDialog.kt', 'w') as f:
    f.write(text)
