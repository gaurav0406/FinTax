import re

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    lines = f.readlines()

# Filter out duplicate imports
seen_imports = set()
cleaned_lines = []

for line in lines:
    if line.startswith("import "):
        if line in seen_imports:
            continue
        seen_imports.add(line)
    cleaned_lines.append(line)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.writelines(cleaned_lines)

