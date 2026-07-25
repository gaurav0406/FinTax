import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Remove the `if (news.category in listOf(...))` wrap around bullet points

old_bullet = """                    if (news.category in listOf("ITR & Tax", "Loans & FDs", "Credit Cards", "Tax")) {
                        InshortsBulletPoint(
                            icon = Icons.Default.Info,
                            iconColor = Color(0xFFFFD54F),
                            label = "How You're Impacted (Tangible/Intangible)",
                            content = news.summaryWhatHappened
                        )
                        InshortsBulletPoint(
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFFA5D6A7),
                            label = "Action To Take (Avoid Risk & Maximize Benefits)",
                            content = news.summaryActionableTakeaway
                        )
                    }"""

new_bullet = """                    InshortsBulletPoint(
                        icon = Icons.Default.Info,
                        iconColor = Color(0xFFFFD54F),
                        label = "How You're Impacted (Tangible/Intangible)",
                        content = news.summaryWhatHappened
                    )
                    InshortsBulletPoint(
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFFA5D6A7),
                        label = "Action To Take (Avoid Risk & Maximize Benefits)",
                        content = news.summaryActionableTakeaway
                    )"""

content = content.replace(old_bullet, new_bullet)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)

