import re

with open("app/src/main/java/com/example/ui/components/DailyDigestCard.kt", "r") as f:
    content = f.read()

# Add mutableStateOf and InAppWebViewDialog
# Add import
import_text = "import androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue\n"
content = content.replace("import androidx.compose.runtime.remember", import_text + "import androidx.compose.runtime.remember")

old_sig = """@Composable
fun DailyDigestCard(
    newsList: List<FinancialNewsEntity>,
    allNewsList: List<FinancialNewsEntity>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {"""

new_sig = """@Composable
fun DailyDigestCard(
    newsList: List<FinancialNewsEntity>,
    allNewsList: List<FinancialNewsEntity>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Top Story") }"""
content = content.replace(old_sig, new_sig)

# Make row clickable
old_row = """                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),"""
new_row = """                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            if (!news.sourceUrl.isNullOrBlank()) {
                                webViewUrlToOpen = news.sourceUrl
                                webViewTitleToOpen = news.sourceName
                            }
                        }
                        .padding(vertical = 8.dp),"""
content = content.replace(old_row, new_row)

# Append InAppWebViewDialog at the end of the composable
old_end = """        }
    }
}"""
new_end = """        }
    }
    
    webViewUrlToOpen?.let { url ->
        InAppWebViewDialog(
            url = url,
            title = webViewTitleToOpen,
            onDismiss = { webViewUrlToOpen = null }
        )
    }
}"""
content = content.replace(old_end, new_end)

with open("app/src/main/java/com/example/ui/components/DailyDigestCard.kt", "w") as f:
    f.write(content)
