package com.example.ui.components

import com.example.data.stripIntroductoryLabels
import com.example.data.getMergedOverview
import com.example.data.getMergedKeyTakeaways
import com.example.data.formatToCrispBullets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle

import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurpleDark
import androidx.compose.material.icons.filled.AutoAwesome

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.style.TextDecoration
import org.json.JSONObject
import org.json.JSONArray
import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.content.Context
import android.content.Intent
import android.app.Activity
import androidx.compose.material.icons.filled.Chat
import com.example.utils.AdMobHelper
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.style.TextOverflow

import com.example.data.FinancialNewsEntity

private fun formatSocialCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
        else -> count.toString()
    }
}

private fun formatRelativeDate(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

@Composable
fun NewsItemCard(
    news: FinancialNewsEntity,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: (() -> Unit)? = null,
    autoPlayAudio: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(true) }
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Financial Action") }

    var showJargonSheet by remember { mutableStateOf(false) }
    var currentJargonTerm by remember { mutableStateOf("") }
    var currentJargonDefinition by remember { mutableStateOf("") }
    var sentiment by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(isExpanded) {
        if (isExpanded && autoPlayAudio && !isPlaying) {
            onPlayAudio()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("news_card_${news.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
                border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 1.0f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Category Badge & Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = MinimalPurplePrimary,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = news.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = news.sourceName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• " + formatRelativeDate(news.publishedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("bookmark_button_${news.id}")
                    ) {
                        Icon(
                            imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (news.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f)
                        )
                    }

                    IconButton(
                        onClick = { shareNewsArticle(context, news) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("share_button_${news.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Article",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (onOpenComments != null) {
                        IconButton(
                            onClick = onOpenComments,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("comment_button_${news.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Comments",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Social Proof Bar (Reads & Shares Counters)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MinimalPurplePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Reads",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${formatSocialCount(news.readCount)} reads",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Surface(
                    color = MinimalPurplePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Shares",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${formatSocialCount(news.shareCount)} shares",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Catchy Headline
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Playback Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .clickable { onPlayAudio() }
                    .testTag("play_audio_button_${news.id}"),
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MinimalPurplePrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Play Audio Digest",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isPlaying) "Playing Audio Summary..." else "Listen Now (60s Digest)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Breakdown - OVERVIEW (4-5 lines narrative)
            NewsBulletPoint(
                icon = Icons.Default.Newspaper,
                iconColor = MaterialTheme.colorScheme.primary,
                label = "OVERVIEW"
            ) {
                JargonText(
                    text = news.getMergedOverview(),
                    jargonTerms = news.jargonTerms,
                    onJargonClick = { term, def ->
                        currentJargonTerm = term
                        currentJargonDefinition = def
                        showJargonSheet = true
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            val keyTakeaways = news.getMergedKeyTakeaways()
            if (keyTakeaways.isNotBlank()) {
                NewsBulletPoint(
                    icon = Icons.Default.CheckCircle,
                    iconColor = MaterialTheme.colorScheme.primary,
                    label = "KEY TAKEAWAYS"
                ) {
                    Text(
                        text = keyTakeaways,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!news.financialActionUrl.isNull_or_blank_safe()) {
                    Button(
                        onClick = {
                            val activity = context as? Activity
                            if (activity != null) {
                                AdMobHelper.showInterstitial(activity) {
                                    webViewUrlToOpen = news.financialActionUrl
                                    webViewTitleToOpen = news.title
                                }
                            } else {
                                webViewUrlToOpen = news.financialActionUrl
                                webViewTitleToOpen = news.title
                            }
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("action_link_${news.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MinimalPurplePrimary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Take Action",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }

                OutlinedButton(
                    onClick = {
                        val activity = context as? Activity
                        if (activity != null) {
                            AdMobHelper.showInterstitial(activity) {
                                webViewUrlToOpen = news.sourceUrl
                                webViewTitleToOpen = "Source: ${news.sourceName}"
                            }
                        } else {
                            webViewUrlToOpen = news.sourceUrl
                            webViewTitleToOpen = "Source: ${news.sourceName}"
                        }
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("source_link_${news.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MinimalPurplePrimary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Source",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }

    webViewUrlToOpen?.let { url ->
        InAppWebViewDialog(
            url = url,
            title = webViewTitleToOpen,
            onDismiss = { webViewUrlToOpen = null }
        )
    }

    if (showJargonSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showJargonSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = currentJargonTerm,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentJargonDefinition,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

}

@Composable
private fun MinimalBulletRow(
    label: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(MinimalPurplePrimary)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = androidx.compose.ui.text.buildAnnotatedString {
                append(label)
                append(" ")
                append(text)
            },
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

private fun String?.isNull_or_blank_safe(): Boolean {
    return this == null || this.trim().isEmpty() || this == "null"
}

fun shareNewsArticle(context: Context, news: FinancialNewsEntity) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, news.title)
        putExtra(
            Intent.EXTRA_TEXT,
            "⚡ ${news.title}\n\nKey Takeaway: ${news.summaryActionableTakeaway}\n\nRead 60-sec update: ${news.sourceUrl}"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share FinTax News"))
    

}


@Composable
fun JargonText(
    text: String,
    jargonTerms: String?,
    onJargonClick: (String, String) -> Unit,
    style: androidx.compose.ui.text.TextStyle
) {
    if (jargonTerms.isNullOrBlank()) {
        Text(text = text, style = style)
        return
    }

    val jargons = jargonTerms.split("|||").mapNotNull { 
        val parts = it.split(": ", limit = 2)
        if (parts.size == 2) parts[0] to parts[1] else null
    }.toMap()

    if (jargons.isEmpty()) {
        Text(text = text, style = style)
        return
    }

    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        val lowerText = text.lowercase()
        
        // Very basic search for jargon (first match wins)
        // For a robust implementation, we'd use regex, but this is a simple approximation
        
        var nextMatchIndex = -1
        var nextMatchWord = ""
        
        while (currentIndex < text.length) {
            nextMatchIndex = -1
            nextMatchWord = ""
            
            for (jargon in jargons.keys) {
                val idx = lowerText.indexOf(jargon.lowercase(), currentIndex)
                if (idx != -1 && (nextMatchIndex == -1 || idx < nextMatchIndex)) {
                    nextMatchIndex = idx
                    nextMatchWord = jargon
                }
            }
            
            if (nextMatchIndex != -1) {
                append(text.substring(currentIndex, nextMatchIndex))
                
                pushStringAnnotation(tag = "JARGON", annotation = nextMatchWord)
                withStyle(style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )) {
                    append(text.substring(nextMatchIndex, nextMatchIndex + nextMatchWord.length))
                }
                pop()
                
                currentIndex = nextMatchIndex + nextMatchWord.length
            } else {
                append(text.substring(currentIndex))
                break
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = style,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "JARGON", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val term = annotation.item
                    val def = jargons.entries.firstOrNull { it.key.equals(term, ignoreCase = true) }?.value ?: ""
                    onJargonClick(term, def)
                }
        }
    )
}


@Composable
private fun NewsBulletPoint(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = androidx.compose.ui.Alignment.Top
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.padding(top = 2.dp).size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 10.sp,
                    color = iconColor,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            content()
        }
    }
}


