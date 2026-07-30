import sys

with open("app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt", "r") as f:
    content = f.read()

target = """            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),"""

replacement = """            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp),"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt", "w") as f:
        f.write(content)
    print("Success Community")
else:
    print("Not found Community")
