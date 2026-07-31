import re

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    content = f.read()

new_content = content.replace(
    "// Target 6 to 7 lines of overview text\n    val overviewSentences = rawSentences.take(7)",
    "// Target 5 to 6 lines of overview text\n    val overviewSentences = rawSentences.take(6)"
)

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
    f.write(new_content)
