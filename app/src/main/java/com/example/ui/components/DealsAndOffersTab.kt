package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.*

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

val sampleDeals = listOf(
    DealItem(
        id = "1",
        brandName = "Amazon",
        title = "Flat 10% Off on Electronics",
        description = "Get a flat 10% discount on all electronics up to ₹1,500 using SBI Credit Cards.",
        offerCode = "SBI10",
        linkUrl = "https://amazon.in",
        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80",
        category = "Technology"
    ),
    DealItem(
        id = "2",
        brandName = "Cleartrip",
        title = "Up to 15% off on Domestic Flights",
        description = "Book your next holiday with HDFC bank cards and get up to 15% off on domestic flights.",
        offerCode = "CTHDFC",
        linkUrl = "https://cleartrip.com",
        imageUrl = "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=600&q=80",
        category = "Travel"
    ),
    DealItem(
        id = "3",
        brandName = "Zerodha",
        title = "Zero Brokerage on Equity Delivery",
        description = "Open a free Demat account today and enjoy zero brokerage on equity delivery investments for life.",
        offerCode = null,
        linkUrl = "https://zerodha.com",
        imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=600&q=80",
        category = "Finance"
    )
)

@Composable
fun DealsAndOffersTab() {
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Offer Details") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Exclusive Deals & Offers",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
                text = "Curated offers to help you save more every day.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sampleDeals) { deal ->
                DealCard(
                    deal = deal,
                    onOpenDeal = { url, title ->
                        webViewUrlToOpen = url
                        webViewTitleToOpen = title
                    }
                )
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
}

@Composable
fun DealCard(deal: DealItem, onOpenDeal: (String, String) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            shape = RoundedCornerShape(8.dp)
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
