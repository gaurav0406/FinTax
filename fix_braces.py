import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()
# Fix DealsAndOffersTab
content = re.sub(r'\}\s*\)\s*\}\s*\}', '}\n}\n', content)
with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()
# Fix InshortsFeedView
content = re.sub(r'\)\s*\}\s*\}', '}\n', content) # Might be dangerous.
with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
