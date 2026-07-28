import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# I messed up and appended the bottom sheet after shareNewsArticle, not at the end of NewsItemCard.
# First remove the bottom sheet code from the end:

bottom_sheet_code = """
    if (showJargonSheet) {
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

# Actually, let's just find the `fun shareNewsArticle` and extract the bottom sheet out of there and put it back in NewsItemCard.
content = content.replace(bottom_sheet_code.strip(), "")
content = content.replace(bottom_sheet_code, "")

# Find the end of NewsItemCard
content = re.sub(r'(\s*if \(webViewUrlToOpen != null\) \{.*?\}\n\}\n\n@Composable\nfun JargonText)', '\n' + bottom_sheet_code + r'\1', content, flags=re.DOTALL)


# Also add @OptIn(ExperimentalMaterial3Api::class) to ModalBottomSheet if needed, or just let it use standard one. 
content = content.replace("androidx.compose.material3.ModalBottomSheet(", "@OptIn(ExperimentalMaterial3Api::class)\n        androidx.compose.material3.ModalBottomSheet(")

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)

