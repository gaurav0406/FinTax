import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

target_navbar = """                NavigationBar(
                    containerColor = if (isDarkTab) Color(0xFF16171E) else MinimalSurfaceVariant,
                    tonalElevation = 2.dp
                ) {"""

replacement_navbar = """                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .background(
                            color = if (isDarkTab) Color(0xFF16171E).copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isDarkTab) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {"""

if target_navbar in content:
    content = content.replace(target_navbar, replacement_navbar)
    with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
        f.write(content)
    print("Success navbar")
else:
    print("Not found navbar")

