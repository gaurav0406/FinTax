440:fun InshortsNewsCardItem(
441-    news: FinancialNewsEntity,
442-    isPlaying: Boolean,
443-    pageIndex: Int,
444-    totalPages: Int,
445-    onPlayAudio: () -> Unit,
446-    onToggleBookmark: () -> Unit,
447-    onOpenActionUrl: (String) -> Unit,
448-    onOpenComments: (() -> Unit)? = null
449-) {
450-    val context = LocalContext.current
451-    val fallbackImage = when (news.category) {
452-        "Credit Cards" -> "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80"
453-        "ITR & Tax" -> "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=1200&q=80"
454-        "Loans & FDs" -> "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80"
455-        "Markets & Mutual Funds" -> "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80"
456-        else -> "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80"
457-    }
458-
459-    val imageUrlToDisplay = news.imageUrl ?: fallbackImage
460-
461-    Box(
462-        modifier = Modifier
463-            .fillMaxSize()
464-            .background(Color(0xFF0F0F12))
465-            .testTag("inshorts_card_${news.id}")
466-    ) {
467-        Column(modifier = Modifier.fillMaxSize()) {
468-            // News Content Body (Starts right at the top)
469-            Column(
470-                modifier = Modifier
471-                    .fillMaxWidth()
472-                    .weight(1f)
473-                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
474-            ) {
475-                // Header Category Pill, Page Counter & Source Actions
476-                Row(
477-                    modifier = Modifier.fillMaxWidth(),
478-                    horizontalArrangement = Arrangement.SpaceBetween,
479-                    verticalAlignment = Alignment.CenterVertically
480-                ) {
481-                    Row(
482-                        verticalAlignment = Alignment.CenterVertically,
483-                        horizontalArrangement = Arrangement.spacedBy(8.dp)
484-                    ) {
485-                        Surface(
486-                            color = MinimalPurplePrimary,
487-                            shape = RoundedCornerShape(20.dp)
488-                        ) {
489-                            Text(
490-                                text = news.category.uppercase(),
491-                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
492-                                style = MaterialTheme.typography.labelSmall.copy(
493-                                    fontWeight = FontWeight.Bold,
494-                                    color = Color.White,
495-                                    fontSize = 10.sp,
496-                                    letterSpacing = 1.sp
497-                                )
498-                            )
499-                        }
500-
501-                        // Page Index Tag
502-                        Surface(
503-                            color = Color.White.copy(alpha = 0.12f),
504-                            shape = RoundedCornerShape(12.dp)
505-                        ) {
506-                            Text(
507-                                text = "${pageIndex + 1} / $totalPages",
508-                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
509-                                style = MaterialTheme.typography.labelSmall.copy(
510-                                    fontWeight = FontWeight.Bold,
511-                                    color = MinimalPurpleLightContainer
512-                                )
513-                            )
514-                        }
515-                    }
516-
517-                    Row(verticalAlignment = Alignment.CenterVertically) {
518-                        Text(
519-                            text = "${news.sourceName} • ${formatRelativeDate(news.publishedAt)}",
520-                            style = MaterialTheme.typography.labelSmall,
521-                            color = Color.White.copy(alpha = 0.8f)
522-                        )
523-
524-                        Spacer(modifier = Modifier.width(6.dp))
525-
526-                        IconButton(
527-                            onClick = onToggleBookmark,
528-                            modifier = Modifier
529-                                .size(32.dp)
530-                                .testTag("inshorts_bookmark_${news.id}")
531-                        ) {
532-                            Icon(
533-                                imageVector = if (news.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
534-                                contentDescription = "Bookmark",
535-                                tint = if (news.isBookmarked) MinimalPurpleLightContainer else Color.White.copy(alpha = 0.7f)
536-                            )
537-                        }
538-
539-                        IconButton(
540-                            onClick = {
541-                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
542-                                    type = "text/plain"
543-                                    putExtra(Intent.EXTRA_SUBJECT, news.title)
544-                                    putExtra(
545-                                        Intent.EXTRA_TEXT,
546-                                        "⚡ ${news.title}\n\nKey Takeaway: ${news.summaryActionableTakeaway}\n\nRead 60-sec update: ${news.sourceUrl}"
547-                                    )
548-                                }
549-                                context.startActivity(Intent.createChooser(shareIntent, "Share Article"))
550-                            },
551-                            modifier = Modifier
552-                                .size(32.dp)
553-                                .testTag("inshorts_share_${news.id}")
554-                        ) {
555-                            Icon(
556-                                imageVector = Icons.Default.Share,
557-                                contentDescription = "Share Article",
558-                                tint = Color.White.copy(alpha = 0.8f)
559-                            )
560-                        }
561-
562-                        if (onOpenComments != null) {
563-                            IconButton(
564-                                onClick = onOpenComments,
565-                                modifier = Modifier
566-                                    .size(32.dp)
567-                                    .testTag("inshorts_comments_${news.id}")
568-                            ) {
569-                                Icon(
570-                                    imageVector = Icons.Default.Chat,
571-                                    contentDescription = "Comments",
572-                                    tint = Color.White.copy(alpha = 0.8f)
573-                                )
574-                            }
575-                        }
576-                    }
577-                }
578-
579-                Spacer(modifier = Modifier.height(10.dp))
580-
581-                // Social Proof Bar (Reads & Shares Counters)
582-                Row(
583-                    modifier = Modifier.fillMaxWidth(),
584-                    verticalAlignment = Alignment.CenterVertically,
585-                    horizontalArrangement = Arrangement.spacedBy(10.dp)
586-                ) {
587-                    Surface(
588-                        color = Color.White.copy(alpha = 0.1f),
589-                        shape = RoundedCornerShape(12.dp)
590-                    ) {
591-                        Row(
592-                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
593-                            verticalAlignment = Alignment.CenterVertically,
594-                            horizontalArrangement = Arrangement.spacedBy(4.dp)
595-                        ) {
596-                            Icon(
597-                                imageVector = Icons.Default.Visibility,
598-                                contentDescription = "Reads",
599-                                tint = Color(0xFF81D4FA),
600-                                modifier = Modifier.size(13.dp)
601-                            )
602-                            Text(
603-                                text = "${formatSocialCount(news.readCount)} reads",
604-                                style = MaterialTheme.typography.labelSmall.copy(
605-                                    fontWeight = FontWeight.Bold,
606-                                    fontSize = 11.sp,
607-                                    color = Color.White.copy(alpha = 0.9f)
608-                                )
609-                            )
610-                        }
611-                    }
612-
613-                    Surface(
614-                        color = Color.White.copy(alpha = 0.1f),
615-                        shape = RoundedCornerShape(12.dp)
616-                    ) {
617-                        Row(
618-                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
619-                            verticalAlignment = Alignment.CenterVertically,
620-                            horizontalArrangement = Arrangement.spacedBy(4.dp)
621-                        ) {
622-                            Icon(
623-                                imageVector = Icons.Default.Share,
624-                                contentDescription = "Shares",
625-                                tint = Color(0xFFA5D6A7),
626-                                modifier = Modifier.size(13.dp)
627-                            )
628-                            Text(
629-                                text = "${formatSocialCount(news.shareCount)} shares",
630-                                style = MaterialTheme.typography.labelSmall.copy(
631-                                    fontWeight = FontWeight.Bold,
632-                                    fontSize = 11.sp,
633-                                    color = Color.White.copy(alpha = 0.9f)
634-                                )
635-                            )
636-                        }
637-                    }
638-                }
639-
640-                Spacer(modifier = Modifier.height(10.dp))
641-
642-                // Clean Bold Headline Title
643-                Text(
644-                    text = news.title,
645-                    style = MaterialTheme.typography.titleLarge.copy(
646-                        fontWeight = FontWeight.Bold,
647-                        fontSize = 20.sp,
648-                        lineHeight = 26.sp,
649-                        color = Color.White
650-                    ),
651-                    maxLines = 3,
652-                    overflow = TextOverflow.Ellipsis
653-                )
654-
655-                Spacer(modifier = Modifier.height(16.dp))
656-
657-                // 4 Bulleted Impact & Financial Analysis Points
658-                Column(
659-                    modifier = Modifier
660-                        .weight(1f)
661-                        .verticalScroll(rememberScrollState()),
662-                    verticalArrangement = Arrangement.spacedBy(10.dp)
663-                ) {
664-                    InshortsBulletPoint(
665-                        icon = Icons.Default.Newspaper,
666-                        iconColor = Color(0xFFCE93D8),
667-                        label = "Summary",
668-                        content = news.summaryText
669-                    )
670-                    InshortsBulletPoint(
671-                        icon = Icons.Default.Group,
672-                        iconColor = Color(0xFF81D4FA),
673-                        label = "Who is Impacted",
674-                        content = news.summaryWhoImpacted
675-                    )
676-                    InshortsBulletPoint(
677-                        icon = Icons.Default.Info,
678-                        iconColor = Color(0xFFFFD54F),
679-                        label = "How You're Impacted",
680-                        content = news.summaryWhatHappened
681-                    )
682-
683-                    val calculatedImpact = news.financialImpactBullets ?: if (news.isFinancialCategory) {
684-                        when (news.category) {
685-                            "ITR & Tax" -> "• Est. Tax Savings: ₹15,600 - ₹25,000/yr for ₹7L-15L bracket\n• Cashflow Impact: +₹2,083/mo net take-home salary boost"
686-                            "Credit Cards" -> "• Direct Cash Impact: -₹350/mo on utility caps or +5% (₹400/mo) on fuel\n• Net Card Yield: ~₹4,800/yr optimized cashback return"
687-                            "Loans & FDs" -> "• Interest Yield: 8.25% p.a. (+₹8,250/yr per ₹1L deposit)\n• Loan EMI Impact: +₹320/mo on ₹50L Home Loan reset"
688-                            "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees funds 48 hrs faster\n• Expected Yield: +1.2% CAGR boost from faster reinvestment"
689-                            "Cars & EV" -> "• Operational Savings: ~₹7,000/mo (₹84,000/yr) vs Petrol vehicle\n• Tax Incentive: Sec 80EEB tax deduction up to ₹1.5 Lakhs"
690-                            else -> "• Financial Gain: Estimated ₹5,000 - ₹12,000 annual net benefit by optimizing financial options."
691-                        }
692-                    } else {
693-                        when (news.category) {
694-                            "Sports" -> "• Championship Standing: India leads WTC table with strong performance\n• Key Highlight: Record-breaking performance in recent fixtures"
695-                            "Education" -> "• Curriculum Shift: Dual-board exam structure & updated entrance syllabi\n• Practical Takeaway: Skill integration across vocational streams"
696-                            "Entertainment" -> "• Streaming Rights: Major platform licensing and high viewer engagement\n• Audience Value: Broader access to premium digital content bundles"
697-                            "Technology Insights" -> "• Infrastructure Boost: Domestic manufacturing expansion and supply chain growth\n• Tech Efficiency: Lower reliance on component imports"
698-                            "AI & New Happenings" -> "• Workflow Automation: Accelerated developer productivity & AI deployment\n• Career Advantage: High demand for generative AI skills"
699-                            else -> "• Key Highlight: Major developments and strategic updates in this domain\n• Practical Takeaway: Core insights and essential knowledge for readers"
700-                        }
701-                    }
702-
703-                    InshortsBulletPoint(
704-                        icon = if (news.isFinancialCategory) Icons.Default.Calculate else Icons.Default.Info,
705-                        iconColor = if (news.isFinancialCategory) Color(0xFF81C784) else Color(0xFFFFB74D),
706-                        label = news.impactSectionTitleMixedCase,
707-                        content = calculatedImpact
708-                    )
709-
710-                    InshortsBulletPoint(
711-                        icon = Icons.Default.CheckCircle,
712-                        iconColor = Color(0xFFA5D6A7),
713-                        label = "Action To Take",
714-                        content = news.summaryActionableTakeaway
715-                    )
716-                }
717-
718-                Spacer(modifier = Modifier.height(12.dp))
719-
720-                // Compact Action Buttons at Bottom
721-                Row(
722-                    modifier = Modifier
723-                        .fillMaxWidth()
724-                        .padding(bottom = 12.dp),
725-                    horizontalArrangement = Arrangement.Center,
726-                    verticalAlignment = Alignment.CenterVertically
727-                ) {
728-                    val actionUrl = news.financialActionUrl ?: news.sourceUrl
729-                    Button(
730-                        onClick = { onOpenActionUrl(actionUrl) },
731-                        modifier = Modifier
732-                            .height(40.dp)
733-                            .testTag("inshorts_apply_button_${news.id}"),
734-                        colors = ButtonDefaults.buttonColors(
735-                            containerColor = MinimalPurplePrimary,
736-                            contentColor = Color.White
737-                        ),
738-                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
739-                        shape = RoundedCornerShape(50)
740-                    ) {
741-                        Icon(
742-                            imageVector = Icons.Default.Launch,
743-                            contentDescription = null,
744-                            modifier = Modifier.size(14.dp)
745-                        )
746-                        Spacer(modifier = Modifier.width(6.dp))
747-                        Text(
748-                            text = "Apply / Learn More",
749-                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
750-                        )
751-                    }
752-
753-                    Spacer(modifier = Modifier.width(10.dp))
754-
755-                    OutlinedButton(
756-                        onClick = { onOpenActionUrl(news.sourceUrl) },
757-                        modifier = Modifier
758-                            .height(40.dp)
759-                            .testTag("inshorts_source_button_${news.id}"),
760-                        colors = ButtonDefaults.outlinedButtonColors(
761-                            contentColor = MinimalPurpleLightContainer
762-                        ),
763-                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
764-                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
765-                        shape = RoundedCornerShape(50)
766-                    ) {
767-                        Icon(
768-                            imageVector = Icons.Default.OpenInNew,
769-                            contentDescription = null,
770-                            modifier = Modifier.size(14.dp)
771-                        )
772-                        Spacer(modifier = Modifier.width(4.dp))
773-                        Text(
774-                            text = "Source",
775-                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f))
776-                        )
777-                    }
778-                }
779-            }
780-        }
781-
782-        // Floating Audio FAB Player
783-        FloatingActionButton(
784-            onClick = onPlayAudio,
785-            modifier = Modifier
786-                .align(Alignment.BottomEnd)
787-                .padding(bottom = 72.dp, end = 20.dp)
788-                .testTag("inshorts_audio_fab_${news.id}"),
789-            containerColor = if (isPlaying) MinimalPurpleDark else MinimalPurplePrimary,
790-            contentColor = Color.White,
791-            shape = CircleShape
792-        ) {
793-            Icon(
794-                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
795-                contentDescription = "Stream 60s Audio Summary",
796-                modifier = Modifier.size(28.dp)
797-            )
798-        }
799-    }
800-}
801-
802-@Composable
803-private fun InshortsBulletPoint(
804-    icon: ImageVector,
805-    iconColor: Color,
806-    label: String,
807-    content: String
808-) {
809-    Surface(
810-        modifier = Modifier.fillMaxWidth(),
811-        shape = RoundedCornerShape(10.dp),
812-        color = Color.White.copy(alpha = 0.08f)
813-    ) {
814-        Row(
815-            modifier = Modifier
816-                .fillMaxWidth()
817-                .padding(horizontal = 10.dp, vertical = 8.dp),
818-            verticalAlignment = Alignment.Top
819-        ) {
820-            Icon(
821-                imageVector = icon,
822-                contentDescription = null,
823-                tint = iconColor,
824-                modifier = Modifier
825-                    .padding(top = 2.dp)
826-                    .size(16.dp)
827-            )
828-
829-            Spacer(modifier = Modifier.width(8.dp))
830-
831-            Column {
832-                Text(
833-                    text = label.uppercase(),
834-                    style = MaterialTheme.typography.labelSmall.copy(
835-                        fontWeight = FontWeight.Bold,
836-                        fontSize = 10.sp,
837-                        color = iconColor,
838-                        letterSpacing = 0.5.sp
839-                    )
840-                )
