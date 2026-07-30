import sys

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

import re
# Replace containerColor = MaterialTheme.colorScheme.surfaceVariant with surface
content = content.replace("containerColor = MaterialTheme.colorScheme.surfaceVariant", "containerColor = MaterialTheme.colorScheme.surface")
content = content.replace("outline.copy(alpha = 0.5f)", "outline.copy(alpha = 1.0f)")

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
print("Patched NewsItemCard!")
