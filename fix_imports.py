import os
import re

for filename in ["app/src/main/java/com/example/ui/MainHomeScreen.kt", "app/src/main/java/com/example/ui/components/InshortsFeedView.kt"]:
    with open(filename, "r") as f:
        content = f.read()
    
    content = re.sub(r'import com.example.ui.components.AdMobNativeExpressCard\n', '', content)
    content = re.sub(r'import com.example.ui.components.DailyDigestCard\n', '', content)
    content = re.sub(r'import com.example.ui.components.DailyCreditCardSpotlightCard\n', '', content)
    content = re.sub(r'import com.example.ui.components.TrendingTweetsRow\n', '', content)
    
    with open(filename, "w") as f:
        f.write(content)
