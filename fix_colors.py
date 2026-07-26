import re

with open('app/src/main/java/com/example/ui/components/CommentSheetDialog.kt', 'r') as f:
    text = f.read()

text = text.replace('focusedTextColor = Color.Black,\n                        unfocusedTextColor = Color.Black,', 'focusedTextColor = MaterialTheme.colorScheme.onSurface,\n                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,')

with open('app/src/main/java/com/example/ui/components/CommentSheetDialog.kt', 'w') as f:
    f.write(text)
