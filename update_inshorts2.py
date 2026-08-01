import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

# Remove DailyDigestSlide logic
content = re.sub(r'if \(dailyDigestList\.isNotEmpty\(\) && currentCat == "All"\) \{\s*slides\.add\(FeedSlide\.DailyDigestSlide\(dailyDigestList\)\)\s*\}', '', content)

# Remove the case rendering the DailyDigestCard
digest_render = """                        is FeedSlide.DailyDigestSlide -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                DailyDigestCard(
                                    newsList = slide.newsList,
                                    allNewsList = allNewsList,
                                    onCategoryClick = onSelectCategory
                                )
                            }
                        }"""
content = content.replace(digest_render, "")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
