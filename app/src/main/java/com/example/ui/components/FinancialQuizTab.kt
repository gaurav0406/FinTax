package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary

data class QuizQuestion(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

val financialQuizQuestions = listOf(
    QuizQuestion(
        id = 1,
        category = "Wealth 101",
        question = "Under the New Tax Regime in India (FY 2024-25), up to what net taxable income limit is tax effectively zero due to Section 87A rebate?",
        options = listOf("₹5 Lakhs", "₹7 Lakhs", "₹10 Lakhs", "₹12 Lakhs"),
        correctIndex = 1,
        explanation = "Under the New Tax Regime, tax rebate under Section 87A ensures individuals with net taxable income up to ₹7 Lakhs pay zero tax."
    ),
    QuizQuestion(
        id = 2,
        category = "Market Signals",
        question = "What is the primary objective of the Reserve Bank of India (RBI) maintaining repo rate stability at 6.5%?",
        options = listOf(
            "Anchoring retail inflation around the 4% target while sustaining economic growth",
            "Eliminating all commercial bank lending activities",
            "Fixing foreign exchange rates against the US Dollar",
            "Providing free loans to corporate conglomerates"
        ),
        correctIndex = 0,
        explanation = "The Monetary Policy Committee (MPC) holds repo rates steady to anchor inflation near 4% while supporting robust 7%+ GDP growth."
    ),
    QuizQuestion(
        id = 3,
        category = "Card Hacks & Perks",
        question = "Which credit card reward strategy yields the highest return value for frequent travelers?",
        options = listOf(
            "Redeeming reward points directly for statement cash back",
            "Transferring reward points to airline and hotel loyalty partner programs",
            "Using reward points for Amazon or Flipkart shopping vouchers",
            "Accumulating points without ever redeeming them"
        ),
        correctIndex = 1,
        explanation = "Transferring reward points to airline and hotel partners (e.g. Marriott, KrisFlyer) often yields 2x to 5x higher value per point."
    ),
    QuizQuestion(
        id = 4,
        category = "Wealth 101",
        question = "What is the key advantage of Rupee Cost Averaging in Mutual Fund SIPs?",
        options = listOf(
            "Buying fewer units when markets are high and more units when prices drop",
            "Guaranteeing fixed 20% annual returns regardless of market volatility",
            "Waiving all exit loads and asset management fees permanently",
            "Eliminating all equity market risks completely"
        ),
        correctIndex = 0,
        explanation = "SIPs automatically purchase more units during market dips and fewer during market peaks, lowering your average cost per unit."
    ),
    QuizQuestion(
        id = 5,
        category = "Tech & AI",
        question = "How is Generative AI transforming digital lending and underwriting in Indian fintech?",
        options = listOf(
            "By replacing human borrowers entirely with robotic avatars",
            "By automating instant loan underwriting, KYC, and risk scoring in under 5 minutes",
            "By increasing interest rates on all personal loans by 50%",
            "By manual paper verification of physical bank passbooks"
        ),
        correctIndex = 1,
        explanation = "GenAI and automated decisioning workflows reduce loan disbursal times from days to under 5 minutes while maintaining strict compliance."
    ),
    QuizQuestion(
        id = 6,
        category = "Startup & Capital",
        question = "What key metric are venture capital investors prioritizing most when evaluating D2C startup funding rounds?",
        options = listOf(
            "High burn rate and vanity social media follower counts",
            "Sustainable unit economics, gross margins, and operating profitability",
            "Number of physical billboards placed in metropolitan cities",
            "Offering free products indefinitely to all consumers"
        ),
        correctIndex = 1,
        explanation = "VC sentiment has shifted toward sustainable unit economics, customer lifetime value (LTV), and profitability over high-burn growth."
    ),
    QuizQuestion(
        id = 7,
        category = "Wealth 101",
        question = "What is the lock-in period for Equity Linked Savings Scheme (ELSS) mutual funds under Section 80C?",
        options = listOf("1 Year", "3 Years", "5 Years", "10 Years"),
        correctIndex = 1,
        explanation = "ELSS offers tax deductions under Section 80C up to ₹1.5 Lakhs with a 3-year lock-in, the shortest among all 80C tax-saving options."
    ),
    QuizQuestion(
        id = 8,
        category = "Card Hacks & Perks",
        question = "What is required by major credit card issuers to unlock complimentary quarterly airport lounge access?",
        options = listOf(
            "Calling customer support 48 hours prior to departure",
            "Meeting minimum spend-based milestone thresholds in the previous quarter",
            "Paying an additional annual fee per lounge visit",
            "Flying exclusively in first class cabins"
        ),
        correctIndex = 1,
        explanation = "Card issuers now require spend prerequisites (e.g. ₹50,000 per quarter) to unlock complimentary domestic and international lounge access."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialQuizTab() {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }

    val question = financialQuizQuestions[currentIndex]

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("financial_quiz_tab"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MinimalPurplePrimary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Financial IQ & Tax Quiz",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Test your financial acumen, earn IQ points, and master wealth management.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!quizCompleted) {
                // Progress Bar & Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of ${financialQuizQuestions.size}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MinimalPurplePrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MinimalPurpleLightContainer
                    ) {
                        Text(
                            text = "Score: $score",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MinimalPurplePrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / financialQuizQuestions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MinimalPurplePrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MinimalPurpleLightContainer
                        ) {
                            Text(
                                text = question.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalPurplePrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = question.question,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                lineHeight = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        question.options.forEachIndexed { index, option ->
                            val isSelected = selectedOption == index
                            val isCorrect = index == question.correctIndex
                            val showResult = selectedOption != null

                            val containerColor = when {
                                showResult && isCorrect -> Color(0xFFE8F5E9)
                                showResult && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                                isSelected -> MinimalPurpleLightContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            }

                            val borderColor = when {
                                showResult && isCorrect -> Color(0xFF4CAF50)
                                showResult && isSelected && !isCorrect -> Color(0xFFE57373)
                                isSelected -> MinimalPurplePrimary
                                else -> Color.Transparent
                            }

                            Card(
                                onClick = {
                                    if (selectedOption == null) {
                                        selectedOption = index
                                        if (index == question.correctIndex) {
                                            score += 20
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .border(1.5.dp, borderColor, RoundedCornerShape(14.dp)),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = containerColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(selectedColor = MinimalPurplePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (showResult && isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50)
                                        )
                                    } else if (showResult && isSelected && !isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = Color(0xFFE57373)
                                        )
                                    }
                                }
                            }
                        }

                        if (selectedOption != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Explanation:",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedOption != null) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Sponsored Ad Banner right above the Next Question button
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MinimalPurplePrimary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SPONSORED AD • Grow Your Wealth",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = MinimalPurplePrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Zero Brokerage & Free Mutual Fund SIPs on Groww & Zerodha. Invest Now!",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            selectedOption = null
                            if (currentIndex < financialQuizQuestions.size - 1) {
                                currentIndex++
                            } else {
                                quizCompleted = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)
                    ) {
                        Text(
                            text = if (currentIndex < financialQuizQuestions.size - 1) "Next Question" else "View Results",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                // Quiz Completed Screen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MinimalPurpleLightContainer,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MinimalPurplePrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Quiz Completed!",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "You scored $score out of ${financialQuizQuestions.size * 20} points",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                currentIndex = 0
                                selectedOption = null
                                score = 0
                                quizCompleted = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)
                        ) {
                            Text(
                                text = "Retake Quiz",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
