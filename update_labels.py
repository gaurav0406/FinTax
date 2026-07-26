import re

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

text = text.replace('label = "Overview",', 'label = "Summary",')
text = text.replace('label = "Direct Outcome",', 'label = "Impacted Users",')
text = text.replace('label = "Direct Message",', 'label = "Reason",')
text = text.replace('label = news.impactSectionTitleMixedCase,', 'label = "Financial Impact/Benefits",')
text = text.replace('label = "Next Step",', 'label = "Action",')

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
    f.write(text)
