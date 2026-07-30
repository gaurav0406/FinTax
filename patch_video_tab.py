import sys

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    content = f.read()

helper_func = '''
fun loadYouTubeVideosFromAssets(context: android.content.Context): List<FinancialNewsEntity> {
    return try {
        val jsonString = context.assets.open("videos.json").bufferedReader().use { it.readText() }
        val jsonArray = org.json.JSONArray(jsonString)
        val list = mutableListOf<FinancialNewsEntity>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val idStr = obj.optString("id", "")
            val title = obj.optString("title", "Financial Short")
            val channelName = obj.optString("channelName", "Finance Shorts")
            val videoUrl = obj.optString("videoUrl", "https://www.youtube.com/watch?v=$idStr")
            val thumbnailUrl = obj.optString("thumbnailUrl", "https://i.ytimg.com/vi/$idStr/hqdefault.jpg")
            
            val cat = when {
                title.contains("Job", true) || title.contains("AI", true) -> "AI & Tech"
                title.contains("VC", true) || title.contains("Startup", true) -> "Startups"
                title.contains("Tax", true) -> "Tax Hacks"
                else -> "Market Reels"
            }
            
            list.add(
                FinancialNewsEntity(
                    id = 9500 + i,
                    title = title,
                    summaryWhatHappened = title,
                    summaryWhoImpacted = "Retail Investors & Salaried Professionals",
                    summaryActionableTakeaway = "Watch this 60s financial reel for practical market tips and tax optimization strategies.",
                    summaryText = "Key breakdown on market dynamics and personal financial growth.",
                    category = cat,
                    financialActionUrl = videoUrl,
                    sourceUrl = videoUrl,
                    sourceName = channelName,
                    imageUrl = thumbnailUrl,
                    audioUrl = null,
                    financialImpactBullets = "• Instant actionable insights for financial growth\\n• Simplified 60s breakdown of market regulations",
                    readCount = 1200 + i * 340,
                    shareCount = 450 + i * 82,
                    commentCount = 180 + i * 24
                )
            )
        }
        list
    } catch (e: Exception) {
        emptyList()
    }
}
'''

target_filter = 'val videoNewsList = newsList.filter { it.sourceUrl.contains("youtube.com") }'
replacement_filter = '''val context = LocalContext.current
    val videoNewsList = remember(newsList) {
        val dbVideos = newsList.filter { it.sourceUrl.contains("youtube.com") }
        if (dbVideos.isNotEmpty()) {
            dbVideos
        } else {
            loadYouTubeVideosFromAssets(context)
        }
    }'''

if "fun loadYouTubeVideosFromAssets" not in content:
    content += helper_func

if target_filter in content:
    content = content.replace(target_filter, replacement_filter)
    with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
        f.write(content)
    print("Success Video Tab Patch")
else:
    print("Target filter not found in Video Tab")

