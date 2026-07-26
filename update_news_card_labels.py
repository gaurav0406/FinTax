import re

with open('app/src/main/java/com/example/ui/components/NewsItemCard.kt', 'r') as f:
    text = f.read()

text = text.replace('text = "WHO IS IMPACTED",', 'text = "IMPACTED USERS",')
text = text.replace('text = "HOW YOU ARE IMPACTED",', 'text = "REASON",')
text = text.replace('text = news.impactSectionTitle,', 'text = "FINANCIAL IMPACT/BENEFITS",')
text = text.replace('text = "WHAT ARE THE NEXT STEPS",', 'text = "ACTION",')

with open('app/src/main/java/com/example/ui/components/NewsItemCard.kt', 'w') as f:
    f.write(text)
