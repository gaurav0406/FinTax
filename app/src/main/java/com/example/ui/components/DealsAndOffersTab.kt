package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.*
import com.example.data.FinancialNewsEntity

data class DealItem(
    val id: String,
    val brandName: String,
    val title: String,
    val description: String,
    val offerCode: String?,
    val linkUrl: String,
    val imageUrl: String,
    val category: String
)

@Composable
fun DealsAndOffersTab(newsList: List<FinancialNewsEntity> = emptyList()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Offer Details") }
    var selectedCategory by remember { mutableStateOf("All Deals") }
    
    val curatedDeals = remember(newsList) {
        val deals = newsList.filter { 
            it.category.contains("Credit", ignoreCase = true) ||
            it.category.contains("Deal", ignoreCase = true) ||
            it.category.contains("Offer", ignoreCase = true)
        }.map { news ->
            DealItem(
                id = news.id.toString(),
                brandName = news.sourceName ?: "Offer",
                title = news.title,
                description = news.summaryWhatHappened.ifBlank { news.summaryText },
                offerCode = null,
                linkUrl = news.sourceUrl,
                imageUrl = news.imageUrl ?: "",
                category = news.category
            )
        }
        deals
    }

    val categories = listOf("All Deals", "Card Hacks & Perks", "Wealth 101", "Financial Markets")

    val filteredDeals = remember(selectedCategory) {
        if (selectedCategory == "All Deals") curatedDeals
        else curatedDeals.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Exclusive Deals & Financial Offers",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
                text = "Handpicked credit card, banking, and investment offers to maximize your savings.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MinimalPurplePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F3F5),
                            labelColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Google AdMob Sponsored Card Placement at top of deals
            

            items(filteredDeals.take(2)) { deal ->
                DealCard(
                    deal = deal,
                    onOpenDeal = { url, title ->
                        try { (context as? android.app.Activity)?.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))) } catch (e: Exception) { e.printStackTrace() }
                        webViewTitleToOpen = title
                    }
                )
            }

            // Second AdMob Sponsored placement in feed
            

            items(filteredDeals.drop(2)) { deal ->
                DealCard(
                    deal = deal,
                    onOpenDeal = { url, title ->
                        try { (context as? android.app.Activity)?.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))) } catch (e: Exception) { e.printStackTrace() }
                        webViewTitleToOpen = title
                    }
                )
            }
        }
    }
}

@Composable
fun DealCard(deal: DealItem, onOpenDeal: (String, String) -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(deal.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = deal.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    color = MinimalPurplePrimary,
                    shape = RoundedCornerShape(bottomEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = deal.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = deal.brandName,
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = deal.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = deal.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (deal.offerCode != null) {
                        Surface(
                            color = MinimalPurpleLightContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable {
                                clipboardManager.setText(AnnotatedString(deal.offerCode))
                                Toast.makeText(context, "Code ${deal.offerCode} copied!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = deal.offerCode,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MinimalPurpleDark
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Code",
                                    tint = MinimalPurpleDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(
                        onClick = { onOpenDeal(deal.linkUrl, deal.brandName) },
                        colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Claim Offer", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
