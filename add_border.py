with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

target = """        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
    )"""

replacement = """        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )"""

if target in text:
    print("Found! Replacing...")
    text = text.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
        f.write(text)
else:
    print("Not found.")
