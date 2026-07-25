import re

with open('app/src/main/java/com/example/ui/components/NewsItemCard.kt', 'r') as f:
    text = f.read()

text = text.replace('MinimalPurpleLightContainer', 'MaterialTheme.colorScheme.surfaceVariant')
text = text.replace('TextPrimary', 'MaterialTheme.colorScheme.onSurfaceVariant')
text = text.replace('TextSecondary', 'MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f)')
text = text.replace('MinimalPurpleDark', 'MaterialTheme.colorScheme.primary')
text = text.replace('Color.White', 'MaterialTheme.colorScheme.onPrimary')

with open('app/src/main/java/com/example/ui/components/NewsItemCard.kt', 'w') as f:
    f.write(text)

