import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip_next = False
for i, line in enumerate(lines):
    if "onOpenReader = { news -> viewModel.openArticleReader(news) }" in line:
        # Check if previous line was also onOpenReader
        if i > 0 and "onOpenReader = { news -> viewModel.openArticleReader(news) }" in lines[i-1]:
            continue
    new_lines.append(line)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.writelines(new_lines)
