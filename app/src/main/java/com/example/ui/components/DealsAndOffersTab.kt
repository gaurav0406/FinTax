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

val curatedDeals = listOf(
    DealItem(
        id = "deal_sbi_cb",
        brandName = "SBI Card",
        title = "SBI Cashback Credit Card - 5% Cashback",
        description = "Get flat 5% cashback on all online shopping transactions. Auto-credited to statement every month with no merchant restriction.",
        offerCode = "SBICB500",
        linkUrl = "https://www.sbicard.com/",
        imageUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=800&q=80",
        category = "Credit Cards"
    ),
    DealItem(
        id = "deal_hdfc_mil",
        brandName = "HDFC Bank",
        title = "HDFC Millennia - 5% Cashback on Swiggy & Zomato",
        description = "Flat 5% cashback on Amazon, Flipkart, Swiggy, Zomato & BookMyShow + 1,000 bonus cash points on card activation.",
        offerCode = "HDFC1000",
        linkUrl = "https://www.hdfcbank.com/",
        imageUrl = "https://images.unsplash.com/photo-1563013544-824ae1b704d3?auto=format&fit=crop&w=800&q=80",
        category = "Credit Cards"
    ),
    DealItem(
        id = "deal_icici_amazon",
        brandName = "ICICI Bank & Amazon",
        title = "Amazon Pay ICICI Card - Lifetime Free",
        description = "Enjoy 5% unlimited cashback for Amazon Prime members, 3% for non-prime members. Zero joining fee and zero annual fee forever.",
        offerCode = "LIFETIME5",
        linkUrl = "https://www.icicibank.com/",
        imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=800&q=80",
        category = "Credit Cards"
    ),
    DealItem(
        id = "deal_zerodha",
        brandName = "Zerodha",
        title = "Zero Brokerage Equity Delivery & Direct MFs",
        description = "Invest in stocks, ETFs and direct mutual funds with ₹0 brokerage charges. Free onboarding for new demat accounts.",
        offerCode = "FREEBROKER",
        linkUrl = "https://zerodha.com/",
        imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=800&q=80",
        category = "Investments"
    ),
    DealItem(
        id = "deal_bajaj_fd",
        brandName = "Bajaj Finance",
        title = "High Interest Fixed Deposit - Up to 8.60% p.a.",
        description = "Secure your savings with AAA rated Bajaj Finance FDs. Earn special interest rates up to 8.60% p.a. for senior citizens & 8.35% for regular depositors.",
        offerCode = "SPECIAL86",
        linkUrl = "https://www.bajajfinserv.in/",
        imageUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=800&q=80",
        category = "Bank & FDs"
    ),
    DealItem(
        id = "deal_idfc_first",
        brandName = "IDFC FIRST Bank",
        title = "7.25% Savings Interest + Monthly Payouts",
        description = "Earn higher returns on your savings balance with monthly interest credits, zero fee banking services, and free airport lounge access debit card.",
        offerCode = "IDFC725",
        linkUrl = "https://www.idfcfirstbank.com/",
        imageUrl = "https://images.unsplash.com/photo-1601597111158-2fceff292cdc?auto=format&fit=crop&w=800&q=80",
        category = "Bank & FDs"
    )
)

@Composable
fun DealsAndOffersTab() {
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Offer Details") }
    var selectedCategory by remember { mutableStateOf("All Deals") }

    val categories = listOf("All Deals", "Credit Cards", "Investments", "Bank & FDs")

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
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Google AdMob Sponsored Card Placement at top of deals
            item {
                AdMobNativeExpressCard(
                    slideIndex = 0,
                    onOpenAd = { url ->
                        webViewUrlToOpen = url
                        webViewTitleToOpen = "Sponsored AdMob Offer"
                    }
                )
            }

            items(filteredDeals.take(2)) { deal ->
                DealCard(
                    deal = deal,
                    onOpenDeal = { url, title ->
                        webViewUrlToOpen = url
                        webViewTitleToOpen = title
                    }
                )
            }

            // Second AdMob Sponsored placement in feed
            item {
                AdMobNativeExpressCard(
                    slideIndex = 1,
                    onOpenAd = { url ->
                        webViewUrlToOpen = url
                        webViewTitleToOpen = "Sponsored Financial Deal"
                    }
                )
            }

            items(filteredDeals.drop(2)) { deal ->
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
