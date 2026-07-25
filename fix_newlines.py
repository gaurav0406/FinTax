with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    text = f.read()

target = """                                            Intent.EXTRA_TEXT,
                                            "⚡ ${news.title}

Key Takeaway: ${news.summaryActionableTakeaway}

Read 60-sec update: ${news.sourceUrl}"
                                        )"""

replacement = """                                            Intent.EXTRA_TEXT,
                                            "⚡ ${news.title}\\n\\nKey Takeaway: ${news.summaryActionableTakeaway}\\n\\nRead 60-sec update: ${news.sourceUrl}"
                                        )"""

if target in text:
    print("Found literal newlines! Fixing...")
    text = text.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
        f.write(text)
else:
    print("Literal newlines not found!")

