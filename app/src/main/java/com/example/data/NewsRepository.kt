package com.example.data

import com.example.network.NewsProcessorService
import com.example.network.YouTubeClient

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
        val profile = dao.getUserProfile().first()
        if (profile == null) {
            dao.saveUserProfile(UserProfileEntity())
        }
    }
    
    private fun normalizeTitle(title: String): String {
        return title.lowercase().filter { it.isLetterOrDigit() }
    }

    suspend fun fetchLiveNewsFromSupabase() {
        var loaded = false

        // 1. Try fetching directly from Supabase REST API
        try {
            val dtos = com.example.network.supabase.SupabaseClient.apiService.getLiveNews()
            if (dtos.isNotEmpty()) {
                val entities = dtos.mapNotNull { it.toEntity() }
                if (entities.isNotEmpty()) {
                    val existingNormalized = dao.getAllNews().first().map { normalizeTitle(it.title) }.toSet()
                    val newEntities = mutableListOf<FinancialNewsEntity>()
                    val seenInBatch = mutableSetOf<String>()

                    for (entity in entities) {
                        val norm = normalizeTitle(entity.title)
                        if (norm.isNotBlank() && norm !in existingNormalized && norm !in seenInBatch) {
                            newEntities.add(entity)
                            seenInBatch.add(norm)
                        }
                    }

                    if (newEntities.isNotEmpty()) {
                        dao.insertNews(newEntities)
                    }
                    loaded = true
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Supabase REST fetch failed: ${e.message}")
        }

        // 2. Fallback to GitHub raw processed dataset if Supabase REST unavailable or unconfigured
        if (!loaded) {
            try {
                val dtos = com.example.network.LiveNewsClient.apiService.getProcessedData()
                if (dtos.isNotEmpty()) {
                    val entities = dtos.mapNotNull { it.toEntity() }
                    if (entities.isNotEmpty()) {
                        val existingNormalized = dao.getAllNews().first().map { normalizeTitle(it.title) }.toSet()
                        val newEntities = mutableListOf<FinancialNewsEntity>()
                        val seenInBatch = mutableSetOf<String>()

                        for (entity in entities) {
                            val norm = normalizeTitle(entity.title)
                            if (norm.isNotBlank() && norm !in existingNormalized && norm !in seenInBatch) {
                                newEntities.add(entity)
                                seenInBatch.add(norm)
                            }
                        }

                        if (newEntities.isNotEmpty()) {
                            dao.insertNews(newEntities)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("NewsRepository", "GitHub processed data fetch failed: ${e.message}")
            }
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
