import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Remove ad and lead gen logic from InshortsFeedView
ad_logic = """                    if (slideCounter > 0 && (slideCounter + 1) % 4 == 0 && (slideCounter + 1) % 8 != 0) {
                        slides.add(FeedSlide.AdSlide(slideCounter))
                        slideCounter++
                    }
                    if (slideCounter > 0 && (slideCounter + 1) % 8 == 0) {
                        slides.add(FeedSlide.LeadGenSlide(slideCounter))
                        slideCounter++
                    }"""

content = content.replace(ad_logic, "")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
