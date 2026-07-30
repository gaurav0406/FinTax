import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

target = """                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {"""

replacement = """                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier.fillMaxSize()
                ) {"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
        f.write(content)
    print("Success LazyColumn")
else:
    print("Not found LazyColumn")

