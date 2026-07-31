package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalBorder
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

@Composable
fun SearchDiscoveryView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectTopic: (String) -> Unit
) {
    val smartTopics = remember {
        listOf(
            "Tech Decoded", "Global Health", "AI Recovery",
            "Economy", "Health Tech", "Vaccines",
            "Mutual Funds", "Credit Cards", "EV Market"
        )
    }

    val searchSuggestions = remember {
        listOf("Fed Interest Rate", "Crypto Taxes 2026", "SIP Calculator", "EV Subsidy", "Credit Card Fee Waiver")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 100.dp)
    ) {
        // Prominent Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search NextGen news, topics, or AI summaries...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MinimalPurplePrimary,
                unfocusedBorderColor = MinimalBorder
            )
        )

        Spacer(Modifier.height(12.dp))

        // Search Suggestions Chips
        Text(
            text = "TRENDING SEARCH TAGS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            searchSuggestions.take(3).forEach { tag ->
                SuggestionChip(
                    onClick = { onSearchQueryChange(tag) },
                    label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Gen Z Reading Analytics & Gamification Panel
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MinimalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔥", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("5-Day Reading Streak!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("1.5x Knowledge Multiplier Active", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MinimalPurplePrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "⏱️ 42m Saved Today",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurplePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MinimalBorder.copy(alpha = 0.5f))
                Spacer(Modifier.height(14.dp))

                // Brain Index & Topic Radar
                Text(
                    text = "🧠 BRAIN INDEX & TOPIC RADAR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MinimalPurplePrimary
                )
                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("AI & Future Tech", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("40%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.40f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = MinimalPurplePrimary,
                            trackColor = MinimalPurpleLightContainer
                        )
                    }

                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Economy & Crypto", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("30%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.30f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF10B981).copy(alpha = 0.2f)
                        )
                    }

                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Global Health & Policy", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("30%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.30f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFF59E0B),
                            trackColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Achievement Badges
                Text(
                    text = "🏆 UNLOCKED ACHIEVEMENTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Perspective Seeker") },
                        leadingIcon = { Text("👁️") }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("Fact Checker") },
                        leadingIcon = { Text("✅") }
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Cross-Device Continuity Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MinimalPurpleLightContainer,
            border = androidx.compose.foundation.BorderStroke(1.dp, MinimalPurplePrimary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MinimalPurplePrimary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Headphones, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cross-Device Audio Sync",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MinimalPurpleDark
                    )
                    Text(
                        text = "🎧 Continue listening from your Smart Speaker (45% completed)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MinimalPurpleDark.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = { /* Sync device */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Sync", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Smart Topics Grid Header
        Text(
            text = "SMART TOPICS (ADAPTS TO YOUR INTERESTS)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MinimalPurplePrimary
        )
        Spacer(Modifier.height(12.dp))

        // Wrap smart topics pills in FlowRow / Grid arrangement
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            smartTopics.chunked(3).forEach { rowTopics ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTopics.forEach { topic ->
                        Surface(
                            onClick = { onSelectTopic(topic) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MinimalBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

