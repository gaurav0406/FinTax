with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

target = '                                                "⚡ ${news.title}\\\n\\\nKey Takeaway: ${news.summaryActionableTakeaway}\\\n\\\nRead 60-sec update: ${news.sourceUrl}"'
replacement = '                                                "⚡ ${news.title}\\n\\nKey Takeaway: ${news.summaryActionableTakeaway}\\n\\nRead 60-sec update: ${news.sourceUrl}"'

if target in text:
    print("Found! Replacing...")
    text = text.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
        f.write(text)
else:
    print("Not found.")

