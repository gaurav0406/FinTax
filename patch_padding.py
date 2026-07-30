import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

target = """        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {"""

replacement = """        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
        f.write(content)
    print("Success padding")
else:
    print("Not found padding")

