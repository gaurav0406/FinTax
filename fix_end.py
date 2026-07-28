import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Fix the end of InshortsBulletPoint
content = content.replace("            }\n\n@Composable\nfun BullishBearishWidget", "            }\n        }\n    }\n}\n\n@Composable\nfun BullishBearishWidget")

# Add import fillMaxHeight
if "import androidx.compose.foundation.layout.fillMaxHeight" not in content:
    content = content.replace("import androidx.compose.foundation.layout.fillMaxSize", "import androidx.compose.foundation.layout.fillMaxSize\nimport androidx.compose.foundation.layout.fillMaxHeight")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)

