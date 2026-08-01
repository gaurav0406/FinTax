import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

content = content.replace("webViewUrlToOpen = url", "try { context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))) } catch (e: Exception) { e.printStackTrace() }")
content = re.sub(r'webViewUrlToOpen\?\.let \{ url ->[\s\S]*?InAppWebViewDialog[\s\S]*?\}', '', content)

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
