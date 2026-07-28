import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# Remove duplicate imports
content = re.sub(r'import androidx\.compose\.material3\.ExperimentalMaterial3Api\n.*?import org\.json\.JSONArray\n', '', content, count=1)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)

