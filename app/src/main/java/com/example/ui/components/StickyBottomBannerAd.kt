package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

@Composable
fun StickyBottomBannerAd(
    onOpenAd: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isBannerVisible by remember { mutableStateOf(true) }
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }

    if (!isBannerVisible) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sticky_bottom_banner_ad"),
        color = Color(0xFF181920),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Ad Header Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFFB74D),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "AD",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                fontSize = 9.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Google AdMob • ca-app-pub-5254258369829746/6926643606",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                IconButton(
                    onClick = { isBannerVisible = false },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Ad Banner",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Banner Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { webViewUrlToOpen = "https://eportal.incometax.gov.in" },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tax Saving FD 8.25% p.a. | Sec 80C Deduction",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "Save up to ₹46,800 in tax before March 31.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { webViewUrlToOpen = "https://eportal.incometax.gov.in" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MinimalPurplePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Check Rates", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    webViewUrlToOpen?.let { url ->
        InAppWebViewDialog(
            url = url,
            title = "Sponsored Financial Offer",
            onDismiss = { webViewUrlToOpen = null }
        )
    }
}
