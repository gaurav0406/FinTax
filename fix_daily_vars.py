import re

with open("app/src/main/java/com/example/ui/components/DailyDigestCard.kt", "r") as f:
    content = f.read()

# Insert the vars right after "val maxCount = ..."
vars_decl = """    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Top Story") }"""

content = content.replace("    val maxCount = categoryCounts.maxOfOrNull { it.second } ?: 1", "    val maxCount = categoryCounts.maxOfOrNull { it.second } ?: 1\n" + vars_decl)

with open("app/src/main/java/com/example/ui/components/DailyDigestCard.kt", "w") as f:
    f.write(content)
