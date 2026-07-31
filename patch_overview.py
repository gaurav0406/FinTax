import re

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    content = f.read()

pattern = r"fun FinancialNewsEntity\.getMergedOverview\(\): String \{.*?\n\}"
replacement = r"""fun FinancialNewsEntity.getMergedOverview(): String {
    val fullText = summaryWhatHappened.replace("•", " ").replace("- ", " ").replace("* ", " ").trim()
    if (fullText.isBlank()) return "Detailed report covering key financial updates, market developments, and strategic policy shifts."
    
    val rawSentences = fullText.split(Regex("(?<=[.!?])\\s+"))
        .map { it.trim() }
        .filter { it.isNotBlank() && it.length > 8 && !it.startsWith("•") }
        .distinct()
    
    // Target 6 to 7 lines of overview text
    val overviewSentences = rawSentences.take(7)
    return if (overviewSentences.isNotEmpty()) {
        overviewSentences.joinToString(" ")
    } else {
        fullText
    }
}"""

new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)
with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
    f.write(new_content)
