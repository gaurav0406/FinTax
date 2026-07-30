import sys

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

target = """                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 72.dp, bottom = 24.dp)"""

replacement = """                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 72.dp, bottom = 100.dp)"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
        f.write(content)
    print("Success Video Reel")
else:
    print("Not found Video Reel")
