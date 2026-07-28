import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# Add needed imports
imports = """
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.style.TextDecoration
import org.json.JSONObject
import org.json.JSONArray
"""

if "import androidx.compose.material3.ModalBottomSheet" not in content:
    content = content.replace("import androidx.compose.material3.Button", imports + "import androidx.compose.material3.Button")

# Add state variables for BottomSheet and Sentiment inside NewsItemCard
state_vars = """
    var showJargonSheet by remember { mutableStateOf(false) }
    var currentJargonTerm by remember { mutableStateOf("") }
    var currentJargonDefinition by remember { mutableStateOf("") }
    var sentiment by remember { mutableStateOf<String?>(null) }
"""

content = re.sub(r'(var webViewTitleToOpen by remember \{ mutableStateOf\("Financial Action"\) \})', r'\1' + '\n' + state_vars, content)

# Inject Key Metrics at the top of the content Column (after Spacer(modifier = Modifier.height(16.dp))
metrics_injection = """
            // Key Metrics Highlights
            val metricsList = news.keyMetrics?.split("|||") ?: emptyList()
            if (metricsList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    metricsList.forEach { metric ->
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = metric,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }
                }
            }
"""

content = re.sub(r'(Text\(\s*text = news\.title,\s*style = MaterialTheme\.typography\.titleMedium\.copy\(.*?\)\s*\)\s*Spacer\(modifier = Modifier\.height\(16\.dp\)\))', r'\1' + '\n' + metrics_injection, content, flags=re.DOTALL)

# Inject Sentiment Poll at the bottom of the card content (before the bottom action bar)
sentiment_injection = """
            // Sentiment Poll
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "How do you feel about this news?",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { sentiment = "Bullish" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sentiment == "Bullish") Color(0xFF2E7D32) else MaterialTheme.colorScheme.surface,
                                contentColor = if (sentiment == "Bullish") Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🚀 Bullish" + if(sentiment != null) " (68%)" else "")
                        }
                        Button(
                            onClick = { sentiment = "Bearish" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sentiment == "Bearish") Color(0xFFC62828) else MaterialTheme.colorScheme.surface,
                                contentColor = if (sentiment == "Bearish") Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📉 Bearish" + if(sentiment != null) " (32%)" else "")
                        }
                    }
                }
            }
"""

content = re.sub(r'(// \-\-\- Expandable Content \-\-\-)', sentiment_injection + r'\n            \1', content)

# Inject Bottom Sheet logic at the very end of NewsItemCard (before the last closing brace)
bottom_sheet = """
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

content = re.sub(r'(\n\}\s*)$', bottom_sheet + r'\1', content)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)

