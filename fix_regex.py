with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    text = f.read()
text = text.replace('val rawSentences = fullText.split(Regex("(?<=[.!?])\s+"))', 'val rawSentences = fullText.split(Regex("(?<=[.!?])\\\\s+"))')
with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
    f.write(text)
