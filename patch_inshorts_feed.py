import sys

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

content = content.replace("iconColor = Color(0xFFCE93D8)", "iconColor = MaterialTheme.colorScheme.primary")
content = content.replace("iconColor = Color(0xFFFFD54F)", "iconColor = MaterialTheme.colorScheme.primary")
content = content.replace("iconColor = if (news.isFinancialCategory) Color(0xFF81C784) else Color(0xFFFFB74D)", "iconColor = MaterialTheme.colorScheme.primary")
content = content.replace("iconColor = Color(0xFFA5D6A7)", "iconColor = MaterialTheme.colorScheme.primary")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
