package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.FinancialNewsEntity
import com.example.data.getMergedKeyTakeaways
import com.example.data.getMergedOverview
import com.example.data.stripIntroductoryLabels
import com.example.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveArticleReaderScreen(
    news: FinancialNewsEntity,
    fontSizeScale: Float = 1.0f,
    isPlayingAudio: Boolean = false,
    onToggleAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isTldrExpanded by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var isReadAlongEnabled by remember { mutableStateOf(true) }
    var activeGlossaryTerm by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isPerspectivesExpanded by remember { mutableStateOf(true) }
    var activePerspective by remember { mutableStateOf("Balanced") } // Balanced, Source A, Source B

    // Audio animation progress
    var audioProgress by remember { mutableFloatStateOf(0.35f) }
    LaunchedEffect(isPlayingAudio) {
        if (isPlayingAudio) {
            while (isPlayingAudio) {
                kotlinx.coroutines.delay(500)
                audioProgress = (audioProgress + 0.02f) % 1.0f
            }
        }
    }

    val bodyFontSize = (16 * fontSizeScale).sp
    val lineSpacing = (24 * fontSizeScale).sp

    val overview = news.getMergedOverview()
    val takeaways = news.getMergedKeyTakeaways()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MinimalPurplePrimary.copy(alpha = 0.1f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Newspaper, contentDescription = null, tint = MinimalPurplePrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Adaptive Reader",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (news.isBookmarked) MinimalPurplePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "${news.title}\n\nRead on NextGen News: ${news.sourceUrl}")
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Article"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Multimodal Audio Player Bar (Sticky)
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onToggleAudio,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MinimalPurplePrimary)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause Audio",
                                    tint = Color.White
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isPlayingAudio) "Narrating Article..." else "AI Audio Companion",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Duration: 2m 45s | Speed: ${playbackSpeed}x",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Speed Toggle
                            FilterChip(
                                selected = playbackSpeed != 1.0f,
                                onClick = {
                                    playbackSpeed = when (playbackSpeed) {
                                        1.0f -> 1.5f
                                        1.5f -> 2.0f
                                        else -> 1.0f
                                    }
                                },
                                label = { Text("${playbackSpeed}x", style = MaterialTheme.typography.labelSmall) }
                            )
                            Spacer(Modifier.width(8.dp))
                            // Read along toggle button
                            IconButton(onClick = { isReadAlongEnabled = !isReadAlongEnabled }) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Read-Along Highlight",
                                    tint = if (isReadAlongEnabled) MinimalPurplePrimary else Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    // Audio Waveform Scrubber
                    LinearProgressIndicator(
                        progress = { audioProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MinimalPurplePrimary,
                        trackColor = MinimalPurpleLightContainer
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Live Badge & Category Tag
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF059669).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "LIVE UPDATE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MinimalPurplePrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = news.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MinimalPurplePrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Article Title (Serif High Contrast Display)
            Text(
                text = news.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = (22 * fontSizeScale).sp,
                    lineHeight = (30 * fontSizeScale).sp
                )
            )

            Spacer(Modifier.height(10.dp))

            // Article Metadata
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("AI Editorial Synthesizer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text("3 min read • Verified Source", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(16.dp))

            // Article Image Header
            if (!news.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(news.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = news.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
            }

            // Interactive TL;DR Section Callout Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MinimalPurpleLightContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MinimalPurplePrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTldrExpanded = !isTldrExpanded }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MinimalPurplePrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("TL;DR Executive Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Icon(
                            imageVector = if (isTldrExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle TLDR"
                        )
                    }

                    AnimatedVisibility(visible = isTldrExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            val eventText = news.summaryWhatHappened?.stripIntroductoryLabels()?.take(120) ?: overview.take(120)
                            Text(
                                text = "• Key Event: $eventText...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "• Strategic Value: ${takeaways.replace("\n", " | ").take(140)}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Article Narrative Body with Read-Along Highlighting
            Text(
                text = "OVERVIEW & BACKGROUND",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MinimalPurplePrimary
            )
            Spacer(Modifier.height(6.dp))

            // Highlighted paragraph if read along active
            Surface(
                color = if (isReadAlongEnabled && isPlayingAudio) Color(0xFFFEF3C7) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = overview,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = bodyFontSize,
                        lineHeight = lineSpacing
                    ),
                    modifier = Modifier.padding(if (isReadAlongEnabled && isPlayingAudio) 8.dp else 0.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Interactive Data Visualization Block
            Text(
                text = "LIVE DATA IMPACT ANALYSIS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MinimalPurplePrimary
            )
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MinimalBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().height(160.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Market Yield Index (+4.2%)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Source: Live Exchange API", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Spacer(Modifier.height(8.dp))

                    // Compose Canvas Line Chart
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path().apply {
                            moveTo(0f, size.height * 0.7f)
                            cubicTo(
                                size.width * 0.25f, size.height * 0.9f,
                                size.width * 0.5f, size.height * 0.2f,
                                size.width * 0.75f, size.height * 0.4f
                            )
                            lineTo(size.width, size.height * 0.1f)
                        }
                        drawPath(
                            path = path,
                            color = MinimalPurplePrimary,
                            style = Stroke(width = 6f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Interactive Inline Glossaries Section
            Text(
                text = "INTERACTIVE GLOSSARY TERMS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MinimalPurplePrimary
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {
                        activeGlossaryTerm = "Liquidity" to "The ease with which an asset can be converted into ready cash without affecting its market price."
                    },
                    label = { Text("Liquidity ?") },
                    leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                AssistChip(
                    onClick = {
                        activeGlossaryTerm = "Yield Curve" to "A line that plots yields, or interest rates, of bonds that have equal credit quality but differing maturity dates."
                    },
                    label = { Text("Yield Curve ?") },
                    leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            // Glossary Popover Definition Card
            activeGlossaryTerm?.let { (term, definition) ->
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MinimalPurpleLightContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Glossary: $term", fontWeight = FontWeight.Bold, color = MinimalPurpleDark)
                            IconButton(onClick = { activeGlossaryTerm = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = MinimalPurpleDark)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(text = definition, style = MaterialTheme.typography.bodySmall, color = MinimalPurpleDark)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Side-by-Side Perspectives Drawer ("Compare Media Coverage")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MinimalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPerspectivesExpanded = !isPerspectivesExpanded }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SwapVert, contentDescription = null, tint = MinimalPurplePrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Compare Media Perspectives", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Icon(
                            imageVector = if (isPerspectivesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Perspectives"
                        )
                    }

                    AnimatedVisibility(visible = isPerspectivesExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            // Perspective Selector Chips
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = activePerspective == "Balanced",
                                    onClick = { activePerspective = "Balanced" },
                                    label = { Text("Balanced View") }
                                )
                                FilterChip(
                                    selected = activePerspective == "Source A",
                                    onClick = { activePerspective = "Source A" },
                                    label = { Text("Reuters") }
                                )
                                FilterChip(
                                    selected = activePerspective == "Source B",
                                    onClick = { activePerspective = "Source B" },
                                    label = { Text("Bloomberg") }
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            val coverageText = when (activePerspective) {
                                "Source A" -> "Reuters Framing: Focuses on institutional capital flows, regulatory compliance metrics, and Central Bank policy alignment."
                                "Source B" -> "Bloomberg Framing: Emphasizes consumer interest rate impact, retail credit card fee hikes, and individual portfolio risk exposure."
                                else -> "Synthesized Balanced Coverage: Merges both macro institutional stability updates with immediate consumer personal finance implications."
                            }

                            Text(
                                text = coverageText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
