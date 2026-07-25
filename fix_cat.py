import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    text = f.read()

text = text.replace("""        if (selectedVideoCategory == "All" || selectedVideoCategory == "60s Shorts") {
            newsList""", """        if (selectedVideoCategory == "All") {
            newsList
        } else if (selectedVideoCategory == "60s Shorts") {
            newsList.filter { it.category == "Video Shorts" }""")

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
    f.write(text)

