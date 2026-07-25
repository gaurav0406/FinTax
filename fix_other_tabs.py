import re
import os

files_to_fix = [
    "app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt",
    "app/src/main/java/com/example/ui/components/PythonPipelineTab.kt",
    "app/src/main/java/com/example/ui/components/AiGeneratorTab.kt",
]

for file_path in files_to_fix:
    if not os.path.exists(file_path):
        continue
    with open(file_path, "r") as f:
        content = f.read()

    # Import TextPrimary and TextSecondary
    if "import com.example.ui.theme.TextSecondary" not in content:
        content = content.replace("import com.example.ui.theme.MinimalPurplePrimary", "import com.example.ui.theme.MinimalPurplePrimary\nimport com.example.ui.theme.TextPrimary\nimport com.example.ui.theme.TextSecondary")
    
    content = content.replace("MaterialTheme.colorScheme.onSurfaceVariant", "TextSecondary")
    content = content.replace("MaterialTheme.colorScheme.onSurface", "TextPrimary")

    with open(file_path, "w") as f:
        f.write(content)
