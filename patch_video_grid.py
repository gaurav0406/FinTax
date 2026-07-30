import sys

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

target = """                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),"""

replacement = """                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start=12.dp, top=12.dp, end=12.dp, bottom=120.dp),"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
        f.write(content)
    print("Success Video Grid")
else:
    print("Not found Video Grid")
