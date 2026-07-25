import re

with open("app/src/main/java/com/example/data/NewsRepository.kt", "r") as f:
    content = f.read()

# Replace fetchLiveNewsFromSupabase body
old_block = """    suspend fun fetchLiveNewsFromSupabase() {
        // We removed the try-catch here so the ViewModel can catch it and show the error in the UI
        val liveNews = com.example.network.LiveNewsClient.apiService.getLiveNews()
        if (liveNews.isNotEmpty()) {
            dao.insertNews(liveNews)
        }
    }"""

new_block = """    suspend fun fetchLiveNewsFromSupabase() {
        val dtos = com.example.network.LiveNewsClient.apiService.getLiveNews()
        if (dtos.isNotEmpty()) {
            val entities = dtos.mapNotNull { dto ->
                if (dto.title == null || dto.sourceUrl == null) return@mapNotNull null
                
                // Parse bullet points
                val what = dto.summary?.getOrNull(0) ?: ""
                val who = dto.summary?.getOrNull(1) ?: ""
                val action = dto.summary?.getOrNull(2) ?: ""
                
                FinancialNewsEntity(
                    title = dto.title,
                    summaryWhatHappened = what,
                    summaryWhoImpacted = who,
                    summaryActionableTakeaway = action,
                    summaryText = dto.summaryText ?: dto.summary?.joinToString(" ") ?: "",
                    category = dto.category ?: "ITR & Tax",
                    financialActionUrl = dto.financialActionUrl,
                    sourceUrl = dto.sourceUrl,
                    sourceName = dto.sourceName ?: "Indian Financial Feed",
                    audioUrl = dto.audioUrl
                )
            }
            if (entities.isNotEmpty()) {
                dao.insertNews(entities)
            }
        }
    }"""

content = content.replace(old_block, new_block)

with open("app/src/main/java/com/example/data/NewsRepository.kt", "w") as f:
    f.write(content)

