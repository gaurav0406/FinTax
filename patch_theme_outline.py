import sys

with open("app/src/main/java/com/example/ui/theme/Color.kt", "r") as f:
    content = f.read()

content = content.replace("val MinimalBorder = Color(0xFFE7E0EC)", "val MinimalBorder = Color(0xFFC4BCC9)")

with open("app/src/main/java/com/example/ui/theme/Color.kt", "w") as f:
    f.write(content)
