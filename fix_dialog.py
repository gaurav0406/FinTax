import re

with open('app/src/main/java/com/example/ui/components/CommentSheetDialog.kt', 'r') as f:
    text = f.read()

text = text.replace('color = MaterialTheme.colorScheme.surface,', 'color = Color.White,')
text = text.replace('.heightIn(max = 440.dp)\n                .clip(RoundedCornerShape(20.dp)),', '.heightIn(max = 440.dp)\n                .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))\n                .clip(RoundedCornerShape(20.dp)),')

with open('app/src/main/java/com/example/ui/components/CommentSheetDialog.kt', 'w') as f:
    f.write(text)
