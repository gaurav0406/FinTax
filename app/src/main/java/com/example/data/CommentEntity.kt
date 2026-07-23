package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val newsId: Int,
    val parentCommentId: Int? = null,
    val userName: String,
    val userCity: String,
    val commentText: String,
    val taggedUser: String? = null,
    val upvotes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
