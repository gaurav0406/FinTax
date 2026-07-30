import sys

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

target = """                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 100.dp),"""

replacement = """                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 120.dp),"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
        f.write(content)
    print("Success Video Actions")
else:
    print("Not found Video Actions")
