package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CommentEntity
import com.example.data.FinancialNewsEntity
import com.example.ui.NewsViewModel
import com.example.ui.theme.MinimalBackground
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CommunityDiscussionsTab(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allComments by viewModel.allComments.collectAsState()
    val allNews by viewModel.newsList.collectAsState()

    var selectedNewsForComments by remember { mutableStateOf<FinancialNewsEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Trending Discussions",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Trending Community Discussions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Engage, discuss opinions & share viral updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (allNews.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active discussions right now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allNews, key = { it.id }) { news ->
                    val newsComments = allComments.filter { it.newsId == news.id }
                    TrendingDiscussionCard(
                        news = news,
                        commentsCount = newsComments.size,
                        topComment = newsComments.maxByOrNull { it.upvotes },
                        onOpenComments = { selectedNewsForComments = news },
                        onShareX = { shareToSocial(context, news, "twitter") },
                        onShareWhatsApp = { shareToSocial(context, news, "whatsapp") }
                    )
                }
            }
        }
    }

    selectedNewsForComments?.let { news ->
        CommentSheetDialog(
            news = news,
            viewModel = viewModel,
            onDismiss = { selectedNewsForComments = null }
        )
    }
}

@Composable
fun TrendingDiscussionCard(
    news: FinancialNewsEntity,
    commentsCount: Int,
    topComment: CommentEntity?,
    onOpenComments: () -> Unit,
    onShareX: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trending_card_${news.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Badge & Source
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MinimalPurpleLightContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = news.category,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MinimalPurpleDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = news.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Article Title
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Actionable Takeaway / Summary
            Text(
                text = news.summaryActionableTakeaway,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Top Highlight Comment snippet if available
            topComment?.let { comment ->
                Surface(
                    color = Color(0xFFF7F7F9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = MinimalPurpleDark
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = comment.userName.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${comment.userName} (${comment.userCity})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = comment.commentText,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = MinimalPurpleDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${comment.upvotes}",
                                fontSize = 10.sp,
                                color = MinimalPurpleDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Bottom Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Comment button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MinimalPurpleLightContainer)
                        .clickable { onOpenComments() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Comments",
                        tint = MinimalPurpleDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$commentsCount Comments",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalPurpleDark
                    )
                }

                // Viral Social Share Quick Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Twitter / X share
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onShareX() },
                        color = Color(0xFF1DA1F2).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "𝕏 Share",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1DA1F2),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // WhatsApp share
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onShareWhatsApp() },
                        color = Color(0xFF25D366).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "WhatsApp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF128C7E),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

fun shareToSocial(context: Context, news: FinancialNewsEntity, platform: String) {
    val textToShare = "🔥 ${news.title}\n\n💡 Key Insight: ${news.summaryActionableTakeaway}\n\nRead update: ${news.sourceUrl}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
        if (platform == "whatsapp") {
            setPackage("com.whatsapp")
        } else if (platform == "twitter") {
            setPackage("com.twitter.android")
        }
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to standard chooser if specific app isn't installed
        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, textToShare)
            },
            "Share via"
        )
        context.startActivity(chooser)
    }
}
