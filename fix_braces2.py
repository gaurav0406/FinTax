import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()
content = content.replace("            }\n        }\n    }\n            )\n    }\n}", "            }\n        }\n    }\n}")
with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()
# Let's see what is around line 366 of InshortsFeedView.kt
