with open('app/src/main/java/com/example/data/NewsRepository.kt', 'r') as f:
    text = f.read()

# I will find the toggleBookmark function and remove everything between `    suspend fun fetchYouTubeVideos` and `suspend fun toggleBookmark`
start_idx = text.find('    suspend fun fetchYouTubeVideos(apiKey: String)')
end_idx = text.find('    suspend fun toggleBookmark')

new_func = """    suspend fun fetchYouTubeVideos(apiKey: String) {
        val categories = listOf(
            "ITR & Tax" to "Indian income tax ITR",
            "Credit Cards" to "Indian credit cards best",
            "Loans & FDs" to "Indian home loans fixed deposits",
            "Markets & Mutual Funds" to "Indian stock market mutual funds",
            "RBI & Policy" to "RBI monetary policy updates"
        )
        
        try {
            val existingVideos = dao.getNewsByCategory("Video Shorts").first()
            if (existingVideos.size > 20) return // Already populated
            
            categories.forEach { (catName, query) ->
                val response = YouTubeClient.apiService.searchVideos(
                    query = query,
                    maxResults = 10,
                    apiKey = apiKey
                )
                val items = response.items ?: return@forEach
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
                        financialImpactBullets = NewsProcessorService.generateFallbackImpact(catName)
                    )
                }.map { it.copy(category = catName, isBookmarked = false) }
                
                if (entities.isNotEmpty()) {
                    dao.insertNews(entities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

"""

text = text[:start_idx] + new_func + text[end_idx:]

with open('app/src/main/java/com/example/data/NewsRepository.kt', 'w') as f:
    f.write(text)

