package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

@Composable
fun LeadGenerationCard(
    slideIndex: Int,
    onOpenExternalLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLoungeCheckerModal by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
            .testTag("lead_gen_card_$slideIndex")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2C1B4D),
                                Color(0xFF160D29)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Surface(
                        color = Color(0xFFEC407A),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "2026 LOUNGE ACCESS ALERT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Is your Credit Card lounge access ending?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            lineHeight = 28.sp,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "18+ popular cards added spend-based rules & revoked free lounge visits in 2026.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Lead Gen Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "QUICK CHECK & BEST REPLACEMENTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurpleLightContainer,
                            letterSpacing = 1.sp
                        )
                    )

                    LeadGenCheckFeatureRow(
                        title = "Instant Eligibility Check",
                        desc = "Select your current bank & card to see if your lounge benefits are active or devalued."
                    )

                    LeadGenCheckFeatureRow(
                        title = "Zero Annual Fee Alternatives",
                        desc = "Discover top replacement cards offering complimentary domestic & international lounge access without quarterly spend caps."
                    )

                    LeadGenCheckFeatureRow(
                        title = "Pre-Approved Offers",
                        desc = "Get fast 2-minute digital approval with instant card generation."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showLoungeCheckerModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("check_lounge_access_button_$slideIndex"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MinimalPurplePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check My Card Lounge Status Now",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    if (showLoungeCheckerModal) {
        LoungeCheckerModalDialog(
            onDismiss = { showLoungeCheckerModal = false },
            onApplyCard = { url ->
                showLoungeCheckerModal = false
                onOpenExternalLink(url)
            }
        )
    }
}

@Composable
private fun LeadGenCheckFeatureRow(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = MinimalPurplePrimary.copy(alpha = 0.2f),
            shape = CircleShape,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MinimalPurpleLightContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun LoungeCheckerModalDialog(
    onDismiss: () -> Unit,
    onApplyCard: (String) -> Unit
) {
    val banks = listOf("HDFC Bank", "SBI Card", "ICICI Bank", "Axis Bank", "Kotak", "IDFC First")
    var selectedBank by remember { mutableStateOf("HDFC Bank") }
    var userCardName by remember { mutableStateOf("") }
    var checkedResult by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1A1A22)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AirplaneTicket,
                            contentDescription = null,
                            tint = MinimalPurpleLightContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lounge Status Checker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Your Card Issuing Bank:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    banks.forEach { bank ->
                        val isSelected = bank == selectedBank
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBank = bank },
                            label = { Text(bank, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MinimalPurplePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = userCardName,
                    onValueChange = { userCardName = it },
                    placeholder = { Text("e.g. Regalia Gold / Millennia / Coral") },
                    label = { Text("Enter Card Variant Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MinimalPurplePrimary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        checkedResult = "DEVALUED: $selectedBank ${userCardName.ifBlank { "Card" }} requires ₹35,000 spend per calendar quarter for 1 lounge pass."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)
                ) {
                    Text("Check Lounge Rule")
                }

                checkedResult?.let { result ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color(0xFF2D161A),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Status Result",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF5252)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "RECOMMENDED REPLACEMENT CARDS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurpleLightContainer,
                            letterSpacing = 0.8.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LeadGenReplacementItem(
                        cardName = "IDFC First Select Credit Card",
                        perks = "4 Free Railway & Domestic Lounge visits / quarter with NO spend criteria. Lifetime FREE.",
                        onApply = { onApplyCard("https://www.idfcfirstbank.com/credit-card") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LeadGenReplacementItem(
                        cardName = "SBI Cashback Credit Card",
                        perks = "5% Direct Cashback on all online shopping. Instant statement credit.",
                        onApply = { onApplyCard("https://www.sbicard.com") }
                    )
                }
            }
        }
    }
}

@Composable
private fun LeadGenReplacementItem(
    cardName: String,
    perks: String,
    onApply: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.06f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cardName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = perks,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
