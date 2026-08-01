package com.example.data

import com.example.network.NewsProcessorService
import com.example.network.YouTubeClient

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NewsRepository(private val dao: FinancialNewsDao) {

    val allNews: Flow<List<FinancialNewsEntity>> = dao.getAllNews()
    val bookmarkedNews: Flow<List<FinancialNewsEntity>> = dao.getBookmarkedNews()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allComments: Flow<List<CommentEntity>> = dao.getAllComments()

    fun getCommentsForNews(newsId: Int): Flow<List<CommentEntity>> = dao.getCommentsForNews(newsId)

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addComment(newsId: Int, text: String, parentCommentId: Int? = null, taggedUser: String? = null, userName: String, city: String) {
        val comment = CommentEntity(
            newsId = newsId,
            parentCommentId = parentCommentId,
            userName = userName,
            userCity = city,
            commentText = text,
            taggedUser = taggedUser,
            upvotes = 1
        )
        dao.insertComment(comment)
    }

    suspend fun upvoteComment(commentId: Int) {
        dao.upvoteComment(commentId)
    }

    fun getNewsByCategory(category: String): Flow<List<FinancialNewsEntity>> {
        return if (category == "All") {
            dao.getAllNews()
        } else {
            dao.getNewsByCategory(category)
        }
    }

    fun searchNews(query: String): Flow<List<FinancialNewsEntity>> {
        return dao.searchNews(query)
    }

    fun normalizeCategory(cat: String?): String {
        if (cat.isNullOrBlank()) return "Wealth 101"
        val upper = cat.uppercase()
        return when {
            upper.contains("CARD") || upper.contains("PERK") || upper.contains("REWARD") -> "Card Hacks & Perks"
            upper.contains("MARKET") || upper.contains("SIGNAL") || upper.contains("MUTUAL") || upper.contains("STOCK") || upper.contains("NIFTY") || upper.contains("SENSEX") || upper.contains("IPO") || upper.contains("LOAN") || upper.contains("FD") || upper.contains("RBI") || upper.contains("POLICY") -> "Market Signals"
            upper.contains("TECH") || upper.contains("AI") || upper.contains("GAMING") || upper.contains("CAR") || upper.contains("EV") || upper.contains("AUTOMOBILE") -> "Tech & AI"
            upper.contains("STARTUP") || upper.contains("CAPITAL") || upper.contains("D2C") || upper.contains("FUNDING") || upper.contains("BUSINESS") || upper.contains("FOUNDER") -> "Startup & Capital"
            upper.contains("WEALTH") || upper.contains("FINANCE") || upper.contains("TAX") || upper.contains("ITR") || upper.contains("EDUCATION") || upper.contains("CRYPTO") || upper.contains("BITCOIN") -> "Wealth 101"
            else -> "Wealth 101"
        }
    }

    suspend fun seedInitialDataIfEmpty(context: android.content.Context? = null) {
        val profile = dao.getUserProfile().first()
        if (profile == null) {
            dao.saveUserProfile(UserProfileEntity())
        }

        dao.deleteAll()

        val freshItems = listOf(
            FinancialNewsEntity(
                id = 101,
                title = "RBI Keeps Repo Rate Unchanged at 6.5%: What It Means For Your Home Loan & EMIs",
                summaryWhatHappened = "The Monetary Policy Committee (MPC) has voted to keep the repo rate steady at 6.5% for the eighth consecutive time, maintaining a balanced stance on inflation control.",
                summaryWhoImpacted = "Home Loan Borrowers, Retail Investors & Fixed Income Depositors",
                summaryActionableTakeaway = "Continue your existing EMIs without upward revision. Consider locking in long-term FDs before rate cut cycles begin.",
                summaryText = "The Reserve Bank of India announced that India's macroeconomic fundamentals remain resilient with GDP growth projected at 7.2% for the current fiscal year. Inflation has moderated towards the 4% target band, allowing the central bank to sustain steady lending rates.",
                category = "Market Signals",
                topicCluster = "RBI Policy & Rates",
                sourceUrl = "https://rbi.org.in",
                sourceName = "RBI Official Bulletin",
                imageUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80",
                publishedAt = System.currentTimeMillis() - 3600000L
            ),
            FinancialNewsEntity(
                id = 102,
                title = "Nifty 50 Crosses 25,500 Milestone Driven by Strong FII Inflows & Banking Rally",
                summaryWhatHappened = "Benchmark Indian indices touched new record highs as foreign institutional investors increased allocations in large-cap banking and IT stocks.",
                summaryWhoImpacted = "Equity Investors, Mutual Fund SIP Holders & Traders",
                summaryActionableTakeaway = "Maintain disciplined SIP allocations. Avoid lump-sum chasing at all-time highs; rebalance portfolio asset allocation.",
                summaryText = "Strong quarterly corporate earnings, robust GST collections crossing ₹1.8 lakh crore, and expanding manufacturing PMI metrics fueled bullish market momentum across sectoral indices.",
                category = "Market Signals",
                topicCluster = "Stock Markets",
                sourceUrl = "https://www.nseindia.com",
                sourceName = "NSE Market Pulse",
                imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80",
                publishedAt = System.currentTimeMillis() - 7200000L
            ),
            FinancialNewsEntity(
                id = 103,
                title = "Major Credit Card Issuers Revise Milestone Benefits & Airport Lounge Access Rules",
                summaryWhatHappened = "Top Indian banks updated spend-based milestone thresholds and quarterly airport lounge access criteria across premium lifestyle credit cards.",
                summaryWhoImpacted = "Credit Cardholders, Frequent Travelers & Reward Maximizers",
                summaryActionableTakeaway = "Review your card spend tracking apps to meet quarterly milestone requirements before billing cycle cutoffs.",
                summaryText = "Card issuers are introducing spend prerequisites (e.g. ₹50,000 per quarter) to unlock complimentary domestic and international lounge access, optimizing reward payouts for active users.",
                category = "Card Hacks & Perks",
                topicCluster = "Credit Card Rewards",
                sourceUrl = "https://www.moneycontrol.com",
                sourceName = "Cards & Perks Desk",
                imageUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80",
                publishedAt = System.currentTimeMillis() - 14400000L
            ),
            FinancialNewsEntity(
                id = 104,
                title = "New Tax Regime Slab Optimizations: How Salaried Professionals Save Up to ₹78,000",
                summaryWhatHappened = "Income tax department guidelines highlight increased standard deductions and revised rebate slabs under the new tax regime for the financial year.",
                summaryWhoImpacted = "Salaried Individuals, Taxpayers & Chartered Accountants",
                summaryActionableTakeaway = "Opt for the new tax regime if your total exemptions through HRA/80C are under ₹3.75 lakh annually.",
                summaryText = "The revised tax slabs provide significant relief to middle-income earners, reducing tax liabilities and simplifying digital ITR filing through pre-filled portal utilities.",
                category = "Wealth 101",
                topicCluster = "Tax Planning",
                sourceUrl = "https://economictimes.indiatimes.com",
                sourceName = "ET Wealth",
                imageUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=1200&q=80",
                publishedAt = System.currentTimeMillis() - 21600000L
            ),
            FinancialNewsEntity(
                id = 105,
                title = "GenAI & Automation Surge Across Indian Fintech: 40% YoY Growth in Digital Lending",
                summaryWhatHappened = "Banks and non-banking financial companies (NBFCs) are scaling generative AI models for instant loan underwriting and fraud detection.",
                summaryWhoImpacted = "Fintech Founders, Tech Professionals & Digital Borrowers",
                summaryActionableTakeaway = "Explore pre-approved digital credit lines via RBI-registered apps with instant KYC verification.",
                summaryText = "Integration of generative AI and automated decisioning workflows has reduced loan disbursal times from days to under 5 minutes while maintaining strict regulatory compliance.",
                category = "Tech & AI",
                topicCluster = "Fintech & AI",
                sourceUrl = "https://techcrunch.com",
                sourceName = "Tech & AI Briefs",
                imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1200&q=80",
                publishedAt = System.currentTimeMillis() - 28800000L
            ),
            FinancialNewsEntity(
                id = 106,
                title = "D2C Startup Funding Rebounds: Venture Capitalists Prioritize Unit Economics & Cash Flow",
                summaryWhatHappened = "Indian direct-to-consumer and retail brands focusing on operating profitability are securing substantial Series B and C funding rounds.",
                summaryWhoImpacted = "Startup Founders, Angel Investors & D2C Brands",
                summaryActionableTakeaway = "Focus on customer lifetime value (LTV) and CAC ratios when presenting financial metrics to institutional investors.",
                summaryText = "Venture capital sentiment has matured, shifting away from high-burn growth toward sustainable unit economics, gross margin expansion, and omnichannel retail presence.",
                category = "Startup & Capital",
                topicCluster = "Venture Capital",
                sourceUrl = "https://economictimes.indiatimes.com",
                sourceName = "Venture Pulse",
                imageUrl = "https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1200&q=80",
                publishedAt = System.currentTimeMillis() - 36000000L
            )
        )

        dao.insertNews(freshItems)
    }

    private fun FinancialNewsEntity.isPlaceholder(): Boolean {
        return title.isBlank() ||
                summaryWhatHappened.equals("placeholder", ignoreCase = true) ||
                (summaryText.contains("Point 1", ignoreCase = true) && summaryWhatHappened.contains("Point 1", ignoreCase = true))
    }

    private fun normalizeTitle(title: String): String {
        return title.lowercase().filter { it.isLetterOrDigit() }
    }

    suspend fun clearCacheAndFetchFresh(context: android.content.Context? = null) {
        try {
            dao.deleteAllUnbookmarked()
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Error clearing local cache: ${e.message}")
        }
        fetchLiveNewsFromSupabase(context)
        val currentCount = dao.getAllNews().first().size
        if (currentCount == 0) {
            seedInitialDataIfEmpty(context)
        }
    }

    suspend fun fetchLiveNewsFromSupabase(context: android.content.Context? = null) {
        // Fetch directly from Supabase REST API
        try {
            var dtos = com.example.network.supabase.SupabaseClient.apiService.getLiveNews(limit = 100)

            if (dtos.isNotEmpty()) {
                val entities = dtos.mapNotNull { it.toEntity() }
                    .filter { !it.isPlaceholder() }
                    .map { it.copy(category = normalizeCategory(it.category)) }
                if (entities.isNotEmpty()) {
                    val seenInBatch = mutableSetOf<String>()
                    val dedupedEntities = mutableListOf<FinancialNewsEntity>()

                    for (entity in entities) {
                        val norm = normalizeTitle(entity.title)
                        if (norm.isNotBlank() && norm !in seenInBatch) {
                            dedupedEntities.add(entity)
                            seenInBatch.add(norm)
                        }
                    }

                    if (dedupedEntities.isNotEmpty()) {
                        // Clear old unbookmarked news from local Room cache so feed reflects fresh Supabase dataset
                        dao.deleteAllUnbookmarked()
                        dao.insertNews(dedupedEntities)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NewsRepository", "Supabase REST fetch error: ${e.message}", e)
        }
    }


    

    suspend fun toggleBookmark(id: Int, currentStatus: Boolean) {
        dao.updateBookmark(id, !currentStatus)
    }

    suspend fun processAndInsertNews(rawText: String, sourceUrl: String): Result<FinancialNewsEntity> {
        val result = NewsProcessorService.summarizeNews(rawText, sourceUrl)
        result.getOrNull()?.let { news ->
            dao.insertSingleNews(news)
        }
        return result
    }

    suspend fun insertCustomNews(news: FinancialNewsEntity) {
        dao.insertSingleNews(news)
    }

    suspend fun deleteNews(id: Int) {
        dao.deleteNewsById(id)
    }
}
