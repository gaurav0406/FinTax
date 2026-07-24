package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class ValueProp(val icon: ImageVector, val title: String, val description: String)

val valuePropositions = listOf(
    ValueProp(Icons.Default.TrendingUp, "Stay Ahead", "Get personalized 60-second financial updates."),
    ValueProp(Icons.Default.Headphones, "Listen on the Go", "Audio summaries let you learn while commuting."),
    ValueProp(Icons.Default.AutoAwesome, "AI Insights", "Understand complex tax and market shifts easily."),
    ValueProp(Icons.Default.Calculate, "Smart Tools", "Calculate taxes and find the best financial deals.")
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ValuePropositionBanner(modifier: Modifier = Modifier) {
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % valuePropositions.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3E5F5)) // Very light purple
            .padding(16.dp)
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                (fadeIn(animationSpec = tween(500)) + slideInHorizontally(animationSpec = tween(500)) { width -> width })
                    .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutHorizontally(animationSpec = tween(500)) { width -> -width })
            }, label = "ValuePropAnimation"
        ) { targetIndex ->
            val valueProp = valuePropositions[targetIndex]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(com.example.ui.theme.MinimalPurplePrimary)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = valueProp.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = valueProp.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = valueProp.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = com.example.ui.theme.TextSecondary
                    )
                }
            }
        }
    }
}
