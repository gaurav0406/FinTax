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

    suspend fun seedInitialDataIfEmpty(context: android.content.Context? = null) {
        val profile = dao.getUserProfile().first()
        if (profile == null) {
            dao.saveUserProfile(UserProfileEntity())
        }

        dao.deletePlaceholders()

        val currentCount = dao.getAllNews().first().size
        if (currentCount == 0 && context != null) {
            try {
                val jsonString = context.assets.open("processed_scraped_data.json").bufferedReader().use { it.readText() }
                val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
                val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, com.example.network.ProcessedScrapedDataDto::class.java)
                val adapter = moshi.adapter<List<com.example.network.ProcessedScrapedDataDto>>(listType)
                val dtos = adapter.fromJson(jsonString)
                if (!dtos.isNullOrEmpty()) {
                    val entities = dtos.mapNotNull { it.toEntity() }.filter { !it.isPlaceholder() }
                    if (entities.isNotEmpty()) {
                        dao.insertNews(entities)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("NewsRepository", "Asset seed failed: ${e.message}")
            }
        }
    }

    private fun FinancialNewsEntity.isPlaceholder(): Boolean {
        return summaryWhatHappened.contains("placeholder", ignoreCase = true) ||
                summaryText.contains("Point 1", ignoreCase = true) ||
                summaryWhatHappened.contains("NLP service", ignoreCase = true) ||
                summaryWhatHappened.contains("This update brings significant", ignoreCase = true) ||
                summaryText.contains("Direct regulatory shift", ignoreCase = true) ||
                financialImpactBullets?.contains("Point 1", ignoreCase = true) == true
    }

    private fun normalizeTitle(title: String): String {
        return title.lowercase().filter { it.isLetterOrDigit() }
    }

    suspend fun clearCacheAndFetchFresh(context: android.content.Context? = null) {
        try {
            dao.deleteAllUnbookmarked()
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error clearing local cache: ${e.message}")
        }
        fetchLiveNewsFromSupabase(context)
    }

    suspend fun fetchLiveNewsFromSupabase(context: android.content.Context? = null) {
        // Ensure initial asset seed if DB is empty and remove any old placeholder rows
        seedInitialDataIfEmpty(context)

        // Fetch directly from Supabase REST API
        try {
            var dtos = com.example.network.supabase.SupabaseClient.apiService.getLiveNews(limit = 100)

            if (dtos.isNotEmpty()) {
                val entities = dtos.mapNotNull { it.toEntity() }.filter { !it.isPlaceholder() }
                if (entities.isNotEmpty()) {
                    val seenInBatch = mutableSetOf<String>()
                    val dedupedEntities = mutableListOf<FinancialNewsEntity>()

                    for (entity in entities) {
                        val norm = normalizeTitle(entity.title)
                        if (norm.isNotBlank() && norm !in seenInBatch) {
                            dedupedEntities.add(entity)
                            seenInBatch.add(norm)
                        }
                    }

                    if (dedupedEntities.isNotEmpty()) {
                        // Clear old unbookmarked news from local Room cache so feed reflects fresh Supabase dataset
                        dao.deleteAllUnbookmarked()
                        dao.insertNews(dedupedEntities)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Supabase REST fetch error: ${e.message}", e)
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
