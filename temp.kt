fun InshortsNewsCardItem(
    news: FinancialNewsEntity,
    isPlaying: Boolean,
    pageIndex: Int,
    totalPages: Int,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenActionUrl: (String) -> Unit,
    onOpenComments: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val fallbackImage = when (news.category) {
        "Credit Cards" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"
        "ITR & Tax" -> "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=1200&q=80"
        "Loans & FDs" -> "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80"
        "Markets & Mutual Funds" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"
        else -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80"
    }

    val imageUrlToDisplay = news.imageUrl ?: fallbackImage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F12))
            .testTag("inshorts_card_${news.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // News Content Body (Starts right at the top)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
            ) {
                // Header Category Pill, Page Counter & Source Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MinimalPurplePrimary,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = news.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        // Page Index Tag
                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${pageIndex + 1} / $totalPages",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalPurpleLightContainer
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${news.sourceName} • ${formatRelativeDate(news.publishedAt)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("inshorts_bookmark_${news.id}")
                        ) {
                            Icon(
                                imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (news.isBookmarked) MinimalPurpleLightContainer else Color.White.copy(alpha = 0.7f)
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, news.title)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "⚡ ${news.title}\n\nKey Takeaway: ${news.summaryActionableTakeaway}\n\nRead 60-sec update: ${news.sourceUrl}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("inshorts_share_${news.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Article",
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        if (onOpenComments != null) {
                            IconButton(
                                onClick = onOpenComments,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("inshorts_comments_${news.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Comments",
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Social Proof Bar (Reads & Shares Counters)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Reads",
                                tint = Color(0xFF81D4FA),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${formatSocialCount(news.readCount)} reads",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Shares",
                                tint = Color(0xFFA5D6A7),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${formatSocialCount(news.shareCount)} shares",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Clean Bold Headline Title
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        color = Color.White
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Bulleted Impact & Financial Analysis Points
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InshortsBulletPoint(
                        icon = Icons.Default.Newspaper,
                        iconColor = Color(0xFFCE93D8),
                        label = "Summary",
                        content = news.summaryText
                    )
                    InshortsBulletPoint(
                        icon = Icons.Default.Group,
                        iconColor = Color(0xFF81D4FA),
                        label = "Who is Impacted",
                        content = news.summaryWhoImpacted
                    )
                    InshortsBulletPoint(
                        icon = Icons.Default.Info,
                        iconColor = Color(0xFFFFD54F),
                        label = "How You're Impacted",
                        content = news.summaryWhatHappened
                    )

                    val calculatedImpact = news.financialImpactBullets ?: if (news.isFinancialCategory) {
                        when (news.category) {
                            "ITR & Tax" -> "• Est. Tax Savings: ₹15,600 - ₹25,000/yr for ₹7L-15L bracket\n• Cashflow Impact: +₹2,083/mo net take-home salary boost"
                            "Credit Cards" -> "• Direct Cash Impact: -₹350/mo on utility caps or +5% (₹400/mo) on fuel\n• Net Card Yield: ~₹4,800/yr optimized cashback return"
                            "Loans & FDs" -> "• Interest Yield: 8.25% p.a. (+₹8,250/yr per ₹1L deposit)\n• Loan EMI Impact: +₹320/mo on ₹50L Home Loan reset"
                            "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees funds 48 hrs faster\n• Expected Yield: +1.2% CAGR boost from faster reinvestment"
                            "Cars & EV" -> "• Operational Savings: ~₹7,000/mo (₹84,000/yr) vs Petrol vehicle\n• Tax Incentive: Sec 80EEB tax deduction up to ₹1.5 Lakhs"
                            else -> "• Financial Gain: Estimated ₹5,000 - ₹12,000 annual net benefit by optimizing financial options."
                        }
                    } else {
                        when (news.category) {
                            "Sports" -> "• Championship Standing: India leads WTC table with strong performance\n• Key Highlight: Record-breaking performance in recent fixtures"
                            "Education" -> "• Curriculum Shift: Dual-board exam structure & updated entrance syllabi\n• Practical Takeaway: Skill integration across vocational streams"
                            "Entertainment" -> "• Streaming Rights: Major platform licensing and high viewer engagement\n• Audience Value: Broader access to premium digital content bundles"
                            "Technology Insights" -> "• Infrastructure Boost: Domestic manufacturing expansion and supply chain growth\n• Tech Efficiency: Lower reliance on component imports"
                            "AI & New Happenings" -> "• Workflow Automation: Accelerated developer productivity & AI deployment\n• Career Advantage: High demand for generative AI skills"
                            else -> "• Key Highlight: Major developments and strategic updates in this domain\n• Practical Takeaway: Core insights and essential knowledge for readers"
                        }
                    }

                    InshortsBulletPoint(
                        icon = if (news.isFinancialCategory) Icons.Default.Calculate else Icons.Default.Info,
                        iconColor = if (news.isFinancialCategory) Color(0xFF81C784) else Color(0xFFFFB74D),
                        label = news.impactSectionTitleMixedCase,
                        content = calculatedImpact
                    )

                    InshortsBulletPoint(
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFFA5D6A7),
                        label = "Action To Take",
                        content = news.summaryActionableTakeaway
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Compact Action Buttons at Bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val actionUrl = news.financialActionUrl ?: news.sourceUrl
                    Button(
                        onClick = { onOpenActionUrl(actionUrl) },
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("inshorts_apply_button_${news.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MinimalPurplePrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Apply / Learn More",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = { onOpenActionUrl(news.sourceUrl) },
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("inshorts_source_button_${news.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MinimalPurpleLightContainer
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Source",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                    }
                }
            }
        }

        // Floating Audio FAB Player
        FloatingActionButton(
            onClick = onPlayAudio,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 72.dp, end = 20.dp)
                .testTag("inshorts_audio_fab_${news.id}"),
            containerColor = if (isPlaying) MinimalPurpleDark else MinimalPurplePrimary,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Stream 60s Audio Summary",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
