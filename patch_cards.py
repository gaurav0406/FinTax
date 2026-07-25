import os

files_with_cards = [
    "app/src/main/java/com/example/ui/components/NewsItemCard.kt",
    "app/src/main/java/com/example/ui/components/DailyDigestCard.kt",
    "app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt",
    "app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt",
    "app/src/main/java/com/example/ui/components/TrendingTweetsRow.kt",
    "app/src/main/java/com/example/ui/components/AdMobNativeExpressCard.kt"
]

border_str = "        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),\n"

for path in files_with_cards:
    if not os.path.exists(path):
        continue
    with open(path, "r") as f:
        content = f.read()

    # Find Card( ... ) and insert border
    # A bit tricky. We can look for "elevation = CardDefaults.cardElevation" and append border after or before it.
    
    content = content.replace(
        "elevation = CardDefaults.cardElevation",
        border_str + "        elevation = CardDefaults.cardElevation"
    )

    with open(path, "w") as f:
        f.write(content)

