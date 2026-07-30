import sys

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

target = """        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),"""

replacement = """        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp),"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
        f.write(content)
    print("Success Deals")
else:
    print("Not found Deals")
