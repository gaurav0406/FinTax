import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

ad_case = """                        is FeedSlide.AdSlide -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AdMobNativeExpressCard(
                                    slideIndex = slide.index,
                                    onOpenAd = openUrlWithAd,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }"""
content = content.replace(ad_case, "")

lead_case = """                        is FeedSlide.LeadGenSlide -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                LeadGenerationCard(modifier = Modifier.padding(16.dp))
                            }
                        }"""
content = content.replace(lead_case, "")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
