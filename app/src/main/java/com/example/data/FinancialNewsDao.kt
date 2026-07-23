package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialNewsDao {

    @Query("SELECT * FROM financial_news ORDER BY publishedAt DESC LIMIT 50")
    fun getAllNews(): Flow<List<FinancialNewsEntity>>

    @Query("SELECT * FROM financial_news WHERE category = :category ORDER BY publishedAt DESC LIMIT 50")
    fun getNewsByCategory(category: String): Flow<List<FinancialNewsEntity>>

    @Query("SELECT * FROM financial_news WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getBookmarkedNews(): Flow<List<FinancialNewsEntity>>

    @Query("SELECT * FROM financial_news WHERE title LIKE '%' || :query || '%' OR summaryText LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun searchNews(query: String): Flow<List<FinancialNewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsList: List<FinancialNewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleNews(news: FinancialNewsEntity): Long

    @Update
    suspend fun updateNews(news: FinancialNewsEntity)

    @Query("UPDATE financial_news SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Int, isBookmarked: Boolean)

    @Query("DELETE FROM financial_news WHERE id = :id")
    suspend fun deleteNewsById(id: Int)

    @Query("DELETE FROM financial_news")
    suspend fun deleteAll()

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // --- Comments ---
    @Query("SELECT * FROM article_comments WHERE newsId = :newsId ORDER BY timestamp ASC")
    fun getCommentsForNews(newsId: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM article_comments ORDER BY timestamp DESC")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("UPDATE article_comments SET upvotes = upvotes + 1 WHERE id = :commentId")
    suspend fun upvoteComment(commentId: Int)
}
