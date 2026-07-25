import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

old_bullet = """                    InshortsBulletPoint(
                        icon = Icons.Default.Group,
                        iconColor = Color(0xFF81D4FA),
                        label = "Who is Impacted",
                        content = news.summaryWhoImpacted
                    )"""

new_bullet = """                    InshortsBulletPoint(
                        icon = Icons.Default.Newspaper,
                        iconColor = Color(0xFFCE93D8),
                        label = "Summary",
                        content = news.summaryText
                    )
                    InshortsBulletPoint(
                        icon = Icons.Default.Group,
                        iconColor = Color(0xFF81D4FA),
                        label = "Who is Impacted",
                        content = news.summaryWhoImpacted
                    )"""

content = content.replace(old_bullet, new_bullet)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
