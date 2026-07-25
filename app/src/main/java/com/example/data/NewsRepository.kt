package com.example.data

import com.example.network.GeminiNewsService
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
        val existing = dao.getAllNews().first()
        if (existing.isEmpty()) {
            val initialComments = SamplePreloadedData.getInitialComments()
            initialComments.forEach { dao.insertComment(it) }
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
    }

    suspend fun toggleBookmark(id: Int, currentStatus: Boolean) {
        dao.updateBookmark(id, !currentStatus)
    }

    suspend fun processAndInsertNews(rawText: String, sourceUrl: String): Result<FinancialNewsEntity> {
        val result = GeminiNewsService.summarizeNewsWithGemini(rawText, sourceUrl)
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
