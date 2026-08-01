import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

content = content.replace("data class DailyDigestSlide(val newsList: List<FinancialNewsEntity>) : FeedSlide", "")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
