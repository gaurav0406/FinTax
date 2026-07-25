import re

with open('app/src/main/java/com/example/data/NewsRepository.kt', 'r') as f:
    text = f.read()

youtube_import = "import com.example.network.YouTubeClient\n"
text = text.replace("import com.example.network.NewsProcessorService", "import com.example.network.NewsProcessorService\n" + youtube_import)

youtube_func = """
    suspend fun fetchYouTubeVideos(apiKey: String) {
        try {
            val response = YouTubeClient.apiService.searchVideos(
                query = "Indian personal finance OR tax OR mutual funds",
                apiKey = apiKey
            )
            val items = response.items ?: return
            val entities = items.mapNotNull { item ->
                val videoId = item.id?.videoId ?: return@mapNotNull null
                val snippet = item.snippet ?: return@mapNotNull null
                val title = snippet.title ?: "Finance Video"
                val desc = snippet.description ?: ""
                val imageUrl = snippet.thumbnails?.high?.url
                val channel = snippet.channelTitle ?: "YouTube"
                
                FinancialNewsEntity(
                    title = title,
                    summaryWhatHappened = desc.take(100),
                    summaryWhoImpacted = channel,
                    summaryActionableTakeaway = "Watch this video for financial insights.",
                    summaryText = desc,
                    category = "Video Shorts",
                    sourceUrl = "https://www.youtube.com/watch?v=$videoId",
                    sourceName = channel,
                    imageUrl = imageUrl,
                    financialImpactBullets = NewsProcessorService.generateFallbackImpact("Personal Finance")
                )
            }
            if (entities.isNotEmpty()) {
                dao.insertNews(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
"""

text = text.replace("    suspend fun toggleBookmark", youtube_func + "\n    suspend fun toggleBookmark")

with open('app/src/main/java/com/example/data/NewsRepository.kt', 'w') as f:
    f.write(text)
