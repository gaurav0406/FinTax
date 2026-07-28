import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

helper = """
@Composable
fun JargonText(
    text: String,
    jargonTerms: String?,
    onJargonClick: (String, String) -> Unit,
    style: androidx.compose.ui.text.TextStyle
) {
    if (jargonTerms.isNullOrBlank()) {
        Text(text = text, style = style)
        return
    }

    val jargons = jargonTerms.split("|||").mapNotNull { 
        val parts = it.split(": ", limit = 2)
        if (parts.size == 2) parts[0] to parts[1] else null
    }.toMap()

    if (jargons.isEmpty()) {
        Text(text = text, style = style)
        return
    }

    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        val lowerText = text.lowercase()
        
        // Very basic search for jargon (first match wins)
        // For a robust implementation, we'd use regex, but this is a simple approximation
        
        var nextMatchIndex = -1
        var nextMatchWord = ""
        
        while (currentIndex < text.length) {
            nextMatchIndex = -1
            nextMatchWord = ""
            
            for (jargon in jargons.keys) {
                val idx = lowerText.indexOf(jargon.lowercase(), currentIndex)
                if (idx != -1 && (nextMatchIndex == -1 || idx < nextMatchIndex)) {
                    nextMatchIndex = idx
                    nextMatchWord = jargon
                }
            }
            
            if (nextMatchIndex != -1) {
                append(text.substring(currentIndex, nextMatchIndex))
                
                pushStringAnnotation(tag = "JARGON", annotation = nextMatchWord)
                withStyle(style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )) {
                    append(text.substring(nextMatchIndex, nextMatchIndex + nextMatchWord.length))
                }
                pop()
                
                currentIndex = nextMatchIndex + nextMatchWord.length
            } else {
                append(text.substring(currentIndex))
                break
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = style,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "JARGON", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val term = annotation.item
                    val def = jargons.entries.firstOrNull { it.key.equals(term, ignoreCase = true) }?.value ?: ""
                    onJargonClick(term, def)
                }
        }
    )
}
"""

if "fun JargonText" not in content:
    content += "\n" + helper

# Now replace Text(text = news.summaryWhatHappened... with JargonText
content = re.sub(
    r'Text\(\s*text = news\.summaryWhatHappened,\s*style = MaterialTheme\.typography\.bodySmall\.copy\(.*?\)\s*\)',
    r'''JargonText(
                text = news.summaryWhatHappened,
                jargonTerms = news.jargonTerms,
                onJargonClick = { term, def ->
                    currentJargonTerm = term
                    currentJargonDefinition = def
                    showJargonSheet = true
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )''',
    content, flags=re.DOTALL
)

content = re.sub(
    r'Text\(\s*text = news\.summaryText,\s*style = MaterialTheme\.typography\.bodySmall\.copy\(.*?\)\s*\)',
    r'''JargonText(
                text = news.summaryText,
                jargonTerms = news.jargonTerms,
                onJargonClick = { term, def ->
                    currentJargonTerm = term
                    currentJargonDefinition = def
                    showJargonSheet = true
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )''',
    content, flags=re.DOTALL
)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)

