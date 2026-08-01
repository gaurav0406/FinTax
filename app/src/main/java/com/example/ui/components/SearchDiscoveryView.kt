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
            "Wealth 101", "Card Hacks & Perks", "Tech & AI"
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

