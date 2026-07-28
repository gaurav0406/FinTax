import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# Limit title to 2 lines
content = re.sub(
    r'(Text\(\s*text = news\.title,\s*style = MaterialTheme\.typography\.titleMedium.*?)\s*\)',
    r'\1,\n                maxLines = 2,\n                overflow = TextOverflow.Ellipsis\n            )',
    content, flags=re.DOTALL
)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)

