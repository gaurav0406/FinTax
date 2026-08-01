import re

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

open_url_old = """    val openUrlWithAd = { url: String, title: String ->
        val activity = context as? Activity
        if (activity != null) {
            AdMobHelper.showInterstitial(activity) {
                webViewUrlToOpen = url
                webViewTitleToOpen = title
            }
        } else {
            webViewUrlToOpen = url
            webViewTitleToOpen = title
        }
    }"""
    
open_url_new = """    val openUrlWithAd = { url: String, title: String ->
        val activity = context as? Activity
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        if (activity != null) {
            AdMobHelper.showInterstitial(activity) {
                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
            }
        } else {
            try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
        }
    }"""
content = content.replace(open_url_old, open_url_new)

# Remove the InAppWebViewDialog instantiation
content = re.sub(r'webViewUrlToOpen\?\.let \{ url ->[\s\S]*?InAppWebViewDialog[\s\S]*?\}', '', content)
with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

# For NewsItemCard, there are two button clicks setting webViewUrlToOpen.
# First one:
btn_1_old = """                            val activity = context as? Activity
                            if (activity != null) {
                                AdMobHelper.showInterstitial(activity) {
                                    webViewUrlToOpen = news.financialActionUrl
                                    webViewTitleToOpen = news.title
                                }
                            } else {
                                webViewUrlToOpen = news.financialActionUrl
                                webViewTitleToOpen = news.title
                            }"""
btn_1_new = """                            val activity = context as? Activity
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(news.financialActionUrl ?: news.sourceUrl))
                            if (activity != null) {
                                AdMobHelper.showInterstitial(activity) {
                                    try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                                }
                            } else {
                                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                            }"""
content = content.replace(btn_1_old, btn_1_new)

btn_2_old = """                        val activity = context as? Activity
                        if (activity != null) {
                            AdMobHelper.showInterstitial(activity) {
                                webViewUrlToOpen = news.sourceUrl
                                webViewTitleToOpen = "Source: ${news.sourceName}"
                            }
                        } else {
                            webViewUrlToOpen = news.sourceUrl
                            webViewTitleToOpen = "Source: ${news.sourceName}"
                        }"""
btn_2_new = """                        val activity = context as? Activity
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(news.sourceUrl))
                        if (activity != null) {
                            AdMobHelper.showInterstitial(activity) {
                                try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                            }
                        } else {
                            try { context.startActivity(intent) } catch (e: Exception) { e.printStackTrace() }
                        }"""
content = content.replace(btn_2_old, btn_2_new)

content = re.sub(r'webViewUrlToOpen\?\.let \{ url ->[\s\S]*?InAppWebViewDialog[\s\S]*?\}', '', content)

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
