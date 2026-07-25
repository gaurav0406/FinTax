package com.example.data

import com.example.network.NewsProcessorService
import com.example.network.YouTubeClient

import com.example.network.SamplePreloadedData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NewsRepository(private val dao: FinancialNewsDao) {

    val allNews: Flow<List<FinancialNewsEntity>> = dao.getAllNews()
    val bookmarkedNews: Flow<List<FinancialNewsEntity>> = dao.getBookmarkedNews()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allComments: Flow<List<CommentEntity>> = dao.getAllComments()

    fun getCommentsForNews(newsId: Int): Flow<List<CommentEntity>> = dao.getCommentsForNews(newsId)

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addComment(newsId: Int, text: String, parentCommentId: Int? = null, taggedUser: String? = null, userName: String, city: String) {
        val comment = CommentEntity(
            newsId = newsId,
            parentCommentId = parentCommentId,
            userName = userName,
            userCity = city,
            commentText = text,
            taggedUser = taggedUser,
            upvotes = 1
        )
        dao.insertComment(comment)
    }

    suspend fun upvoteComment(commentId: Int) {
        dao.upvoteComment(commentId)
    }

    fun getNewsByCategory(category: String): Flow<List<FinancialNewsEntity>> {
        return if (category == "All") {
            dao.getAllNews()
        } else {
            dao.getNewsByCategory(category)
        }
    }

    fun searchNews(query: String): Flow<List<FinancialNewsEntity>> {
        return dao.searchNews(query)
    }

    suspend fun seedInitialDataIfEmpty() {
        val initialNews = SamplePreloadedData.getInitialNewsList()
        val existing = dao.getAllNews().first()
        if (existing.isEmpty()) {
            dao.insertNews(initialNews)
            val initialComments = SamplePreloadedData.getInitialComments()
            initialComments.forEach { dao.insertComment(it) }
        } else {
            // Update any existing records that have null financialImpactBullets
            val preloadedMap = initialNews.associateBy { it.title }
            existing.forEach { item ->
                if (item.financialImpactBullets.isNullOrBlank()) {
                    val preloaded = preloadedMap[item.title]
                    val impactToSet = preloaded?.financialImpactBullets 
                        ?: NewsProcessorService.generateFallbackImpact(item.category)
                    dao.updateNews(item.copy(financialImpactBullets = impactToSet))
                }
            }
        }
        val profile = dao.getUserProfile().first()
        if (profile == null) {
            dao.saveUserProfile(UserProfileEntity())
        }
    }
    
    suspend fun fetchLiveNewsFromSupabase() {
        val dtos = com.example.network.LiveNewsClient.apiService.getLiveNews()
        if (dtos.isNotEmpty()) {
            val entities = dtos.mapNotNull { dto ->
                if (dto.title == null || dto.sourceUrl == null) return@mapNotNull null
                
                // Parse bullet points
                val what = dto.summary?.getOrNull(0) ?: ""
                val who = dto.summary?.getOrNull(1) ?: ""
                val action = dto.summary?.getOrNull(2) ?: ""
                val category = dto.category ?: "ITR & Tax"
                
                FinancialNewsEntity(
                    title = dto.title,
                    summaryWhatHappened = what,
                    summaryWhoImpacted = who,
                    summaryActionableTakeaway = action,
                    summaryText = dto.summaryText ?: dto.summary?.joinToString(" ") ?: "",
                    category = category,
                    financialActionUrl = dto.financialActionUrl,
                    sourceUrl = dto.sourceUrl,
                    sourceName = dto.sourceName ?: "Indian Financial Feed",
                    audioUrl = dto.audioUrl,
                    financialImpactBullets = NewsProcessorService.generateFallbackImpact(category)
                )
            }
            if (entities.isNotEmpty()) {
                dao.insertNews(entities)
            }
        }
    }


    suspend fun fetchYouTubeVideos(apiKey: String) {
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

    suspend fun toggleBookmark(id: Int, currentStatus: Boolean) {
        dao.updateBookmark(id, !currentStatus)
    }

    suspend fun processAndInsertNews(rawText: String, sourceUrl: String): Result<FinancialNewsEntity> {
        val result = NewsProcessorService.summarizeNews(rawText, sourceUrl)
        result.getOrNull()?.let { news ->
            dao.insertSingleNews(news)
        }
        return result
    }

    suspend fun insertCustomNews(news: FinancialNewsEntity) {
        dao.insertSingleNews(news)
    }

    suspend fun deleteNews(id: Int) {
        dao.deleteNewsById(id)
    }
}
