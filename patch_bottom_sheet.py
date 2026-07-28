import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

bottom_sheet_code = """
    if (showJargonSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showJargonSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = currentJargonTerm,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentJargonDefinition,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
"""

# The bottom sheet was wrongly placed inside shareNewsArticle earlier and we tried to fix it but maybe missed the exact location of the end of the Composable.
# Let's insert it right after `InAppWebViewDialog` which is at the very end of NewsItemCard.
content = re.sub(r'(\s*InAppWebViewDialog\(\s*url = url,\s*title = webViewTitleToOpen,\s*onDismiss = \{ webViewUrlToOpen = null \}\s*\)\s*\})', r'\1\n' + bottom_sheet_code, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)

