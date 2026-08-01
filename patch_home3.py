import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "onOpenComments: ((FinancialNewsEntity) -> Unit)? = null,",
    "onOpenComments: ((FinancialNewsEntity) -> Unit)? = null,\n    onOpenReader: (FinancialNewsEntity) -> Unit,"
)

content = content.replace(
    "onOpenComments = { news -> selectedNewsForComments = news }",
    "onOpenComments = { news -> selectedNewsForComments = news },\n                            onOpenReader = { news -> viewModel.openArticleReader(news) }"
)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
