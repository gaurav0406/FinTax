import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Replace the AdSlide and LeadGenSlide blocks with empty string
content = re.sub(r'is FeedSlide\.AdSlide -> \{[^\}]+\}[^\}]+\}', '', content)
content = re.sub(r'is FeedSlide\.LeadGenSlide -> \{[^\}]+\}[^\}]+\}', '', content)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
