import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

content = content.replace("import com.example.data.FinancialNewsEntity\n\n@Composable\nfun DealsAndOffersTab", "@Composable\nfun DealsAndOffersTab")

if "import com.example.data.FinancialNewsEntity" not in content:
    content = content.replace("import com.example.ui.theme.*", "import com.example.ui.theme.*\nimport com.example.data.FinancialNewsEntity")

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
