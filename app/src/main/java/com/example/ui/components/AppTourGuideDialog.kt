package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.*

data class TourStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val buttonName: String
)

@Composable
fun AppTourGuideDialog(
    userName: String,
    onDismiss: () -> Unit
) {
    val steps = remember {
        listOf(
            TourStep(
                title = "Welcome to FinTax, ${userName}!",
                description = "Here is a quick walkthrough of your new financial command center. Let's explore how each button and feature helps you stay ahead.",
                icon = Icons.Default.AccountCircle,
                buttonName = "Get Started"
            ),
            TourStep(
                title = "1. Financial News & Original Source",
                description = "Browse real-time market updates, tax changes, and credit card deals. Tap any news card to open the Original Source instantly in your browser without any login wall.",
                icon = Icons.Default.Newspaper,
                buttonName = "News Feed Tab"
            ),
            TourStep(
                title = "2. Inshorts Swipe Feed",
                description = "Use the Inshorts feed toggle to swipe vertically through bite-sized summaries, listen to audio summaries, and view 'What's Changed?' analytics badges.",
                icon = Icons.Default.TouchApp,
                buttonName = "Swipe Feed Button"
            ),
            TourStep(
                title = "3. Community Discussions",
                description = "Connect with fellow taxpayers and investors. Share thoughts, reply to comments, and upvote valuable financial insights.",
                icon = Icons.Default.Forum,
                buttonName = "Discussions Tab"
            ),
            TourStep(
                title = "4. Advanced Tax Calculator",
                description = "Instantly compute and compare Old vs. New tax regimes with professional deductions and recommendations.",
                icon = Icons.Default.Calculate,
                buttonName = "Tax Calculator Tab"
            ),
            TourStep(
                title = "5. Deals & Offers",
                description = "Discover exclusive credit card rewards, milestone bonuses, and bank offers tailored for Indian consumers.",
                icon = Icons.Default.LocalOffer,
                buttonName = "Deals & Offers Tab"
            ),
            TourStep(
                title = "6. Bookmarks & Profile",
                description = "Save your favorite stories for offline reading and customize your financial profile anytime in settings.",
                icon = Icons.Default.Bookmark,
                buttonName = "Bookmarks & Profile"
            )
        )
    }

    var currentStep by remember { mutableStateOf(0) }
    val step = steps[currentStep]

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.75f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .testTag("tour_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Skip Tour",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    // Step indicator
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Step ${currentStep + 1} of ${steps.size}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pointer / Arrow indicator explaining the button
                    Surface(
                        color = MinimalPurpleLightContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = MinimalPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Feature: ${step.buttonName}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MinimalPurplePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentStep > 0) {
                            OutlinedButton(
                                onClick = { currentStep-- },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Previous")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Button(
                            onClick = {
                                if (currentStep < steps.size - 1) {
                                    currentStep++
                                } else {
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)
                        ) {
                            Text(if (currentStep == steps.size - 1) "Got It, Let's Go!" else "Next")
                        }
                    }
                }
            }
        }
    }
}
}
