import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

# Let's inspect the file starting around line 996
lines = text.split('\n')
for i, line in enumerate(lines):
    if i >= 940 and i <= 950:
        pass # print(f"{i+1}: {line}")

