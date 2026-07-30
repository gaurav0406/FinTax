import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

target = "import androidx.compose.foundation.background"
replacement = "import androidx.compose.foundation.background\nimport androidx.compose.foundation.border"

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
        f.write(content)
    print("Success import")
else:
    print("Not found import")
