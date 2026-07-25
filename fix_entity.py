with open('app/src/main/java/com/example/data/FinancialNewsEntity.kt', 'r') as f:
    text = f.read()

text = text.replace('"Cars & EV" -> true', '"Cars & EV", "Video Shorts" -> true')

with open('app/src/main/java/com/example/data/FinancialNewsEntity.kt', 'w') as f:
    f.write(text)
