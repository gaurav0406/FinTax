import re

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

# Remove the Impacted Users bullet point block
block_to_remove = r'InshortsBulletPoint\(\s*icon = Icons\.Default\.Group,\s*iconColor = Color\(0xFF81D4FA\),\s*label = "Impacted Users",\s*content = news\.summaryWhoImpacted\s*\)'

text = re.sub(block_to_remove, '', text, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
    f.write(text)
