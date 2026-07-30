import sys

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

target = """                // Bottom Section: Metrics & Actions
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {"""

replacement = """                // Bottom Section: Metrics & Actions
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp)
                ) {"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
        f.write(content)
    print("Success Inshorts Padding")
else:
    print("Not found Inshorts Padding")
