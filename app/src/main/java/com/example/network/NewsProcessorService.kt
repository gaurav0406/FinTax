package com.example.network

import com.example.data.FinancialNewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NewsProcessorService {

    suspend fun summarizeNews(rawText: String, sourceUrl: String = "https://eportal.incometax.gov.in"): Result<FinancialNewsEntity> = withContext(Dispatchers.IO) {
        Result.success(createFallbackEntity(rawText, sourceUrl))
    }

    fun generateFallbackImpact(category: String): String {
        return when (category) {
            "ITR & Tax" -> "• Estimated Tax Savings: ₹15,600 - ₹25,000/yr for ₹7L-15L bracket\n• Cash Flow Impact: +₹2,083/mo net take-home salary increase"
            "Credit Cards" -> "• Direct Cash Impact: -₹350/mo on utility fees or +5% (₹400/mo) fuel waiver\n• Net Annual Return: ~₹4,800/yr optimized card savings"
            "Loans & FDs" -> "• Interest Yield / Outlay: 8.25% return (+₹8,250/yr on ₹1L deposit) or +₹320/mo on ₹50L Home Loan EMI"
            "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees up cash 48 hours earlier for reinvestment\n• Portfolio Yield: +1.2% CAGR impact from reduced holding lag"
            "Cars & EV" -> "• Operational Savings: ~₹7,000/mo (₹84,000/yr) vs Petrol vehicle\n• Tax Incentive: Sec 80EEB tax deduction up to ₹1.5 Lakhs"
            "FinTech & Crypto" -> "• Transaction Velocity: Instant cross-border settlement with 0% fee\n• Digital Rupee CBDC: 1% cashback on offline wallet UPI payments"
            "Smart Investing" -> "• Algo Trading Yield: +3.8% alpha over benchmark index\n• Expense Ratio Savings: 0.15% direct plan low-cost SIP advantage"
            "Personal Finance" -> "• Emergency Fund Security: 6-month liquidity buffer preserved\n• Health Insurance Benefit: ₹50,000 tax deduction under Sec 80D"
            "Sports" -> "• Championship Standing: India leads WTC table with strong performance\n• Key Highlight: Record-breaking individual and team statistics"
            "Education" -> "• Curriculum Shift: Dual-board exam structure & updated entrance syllabi\n• Practical Takeaway: Skill integration across vocational streams"
            "Entertainment" -> "• Distribution Milestone: Record multi-platform streaming rights agreement\n• Viewership Impact: Broader audience reach and digital catalog expansion"
            "Technology Insights" -> "• Infrastructure Boost: Domestic semiconductor manufacturing expansion\n• Efficiency Gain: Modern hardware architecture and reduced component imports"
            "AI & New Happenings" -> "• Workflow Automation: Accelerated developer productivity & AI deployment\n• Skill Demand: High career opportunities for generative AI specialists"
            "Startup Ecosystem" -> "• Ecosystem Growth: Expanded funding rounds for tech ventures\n• Job Creation: High demand for skilled software engineering talent"
            else -> "• Key Highlight: Major developments and strategic updates in this domain\n• Practical Takeaway: Essential insights and core knowledge for readers"
        }
    }

    private fun createFallbackEntity(rawText: String, sourceUrl: String): FinancialNewsEntity {
        val snippet = rawText.take(150).replace("\n", " ")
        val category = when {
            rawText.contains("credit card", true) || rawText.contains("reward", true) -> "Credit Cards"
            rawText.contains("fd", true) || rawText.contains("loan", true) || rawText.contains("interest", true) -> "Loans & FDs"
            rawText.contains("mutual fund", true) || rawText.contains("market", true) || rawText.contains("sip", true) -> "Markets & Mutual Funds"
            rawText.contains("rbi", true) || rawText.contains("repo rate", true) || rawText.contains("policy", true) -> "RBI & Policy"
            else -> "ITR & Tax"
        }

        val actionUrl = when (category) {
            "ITR & Tax" -> "https://eportal.incometax.gov.in"
            "Credit Cards" -> "https://www.sbicard.com"
            "RBI & Policy" -> "https://www.rbi.org.in"
            else -> "https://www.moneycontrol.com"
        }

        val p1 = "What Happened: New guidelines announced for $category regarding $snippet..."
        val p2 = "Who is Impacted: Salaried individuals, individual taxpayers, and retail investors."
        val p3 = "Actionable Takeaway: Review official portal notices before the next tax quarter deadline."

        val fallbackImpact = when (category) {
            "ITR & Tax" -> "• Estimated Tax Savings: ₹15,600 - ₹25,000/yr for ₹7L-15L bracket\n• Cash Flow Impact: +₹2,083/mo net take-home salary increase"
            "Credit Cards" -> "• Direct Cash Impact: -₹350/mo on utility fees or +5% (₹400/mo) fuel waiver\n• Net Annual Return: ~₹4,800/yr optimized card savings"
            "Loans & FDs" -> "• Interest Yield / Outlay: 8.25% return (+₹8,250/yr on ₹1L deposit) or +₹320/mo on ₹50L Home Loan EMI"
            "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees up cash 48 hours earlier for reinvestment\n• Portfolio Yield: +1.2% CAGR impact from reduced holding lag"
            else -> "• Quantifiable Benefit: Estimated ₹5,000 - ₹12,000 annual net gain by optimizing financial strategy."
        }

        val summaryText = "$p1 $p2 $p3"

        return FinancialNewsEntity(
            title = "Key $category Update for Indian Taxpayers",
            summaryWhatHappened = p1,
            summaryWhoImpacted = p2,
            summaryActionableTakeaway = p3,
            summaryText = summaryText,
            category = category,
            financialActionUrl = actionUrl,
            sourceUrl = sourceUrl,
            sourceName = "Indian Financial News Feed",
            financialImpactBullets = fallbackImpact,
            publishedAt = System.currentTimeMillis()
        )
    }
}
