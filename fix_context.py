import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

content = content.replace("fun DealsAndOffersTab(newsList: List<FinancialNewsEntity> = emptyList()) {", "fun DealsAndOffersTab(newsList: List<FinancialNewsEntity> = emptyList()) {\n    val context = androidx.compose.ui.platform.LocalContext.current")

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
