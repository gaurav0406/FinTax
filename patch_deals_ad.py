import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

old_ad_call = "AdMobNativeExpressCard()"
new_ad_call = """AdMobNativeExpressCard(
                    slideIndex = 0,
                    onOpenAd = { url ->
                        webViewUrlToOpen = url
                        webViewTitleToOpen = "Sponsored Offer"
                    }
                )"""
content = content.replace(old_ad_call, new_ad_call)

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
