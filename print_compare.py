import re

with open("app/src/main/java/com/example/ui/components/AdaptiveArticleReaderScreen.kt", "r") as f:
    content = f.read()

lines = content.splitlines()
for i, line in enumerate(lines):
    if "Compare Media Perspectives" in line:
        start = max(0, i - 15)
        end = min(len(lines), i + 25)
        print("\n".join(lines[start:end]))
        break
