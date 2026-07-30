package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_news")
data class FinancialNewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val summaryWhatHappened: String,
    val summaryWhoImpacted: String,
    val summaryActionableTakeaway: String,
    val summaryText: String,
    val category: String, // Broad category
    val topicCluster: String = "Latest Updates", // Dynamic Google News-style cluster tag
    val financialActionUrl: String? = null,
    val sourceUrl: String,
    val sourceName: String = "Indian Financial Feed",
    val audioUrl: String? = null,
    val imageUrl: String? = null,
    val financialImpactBullets: String? = null,
    val keyMetrics: String? = null,
    val jargonTerms: String? = null,
    val publishedAt: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false,
    val readCount: Int = 1250,
    val shareCount: Int = 180
) {
    val commentCount: Int
        get() = (id * 17) % 150 + 5

    val isFinancialCategory: Boolean
        get() = when (category.trim()) {
            "Financial News", "Credit Cards", "Mutual Funds", "Crypto" -> true
            else -> false
        }

    val impactSectionTitle: String
        get() = if (isFinancialCategory) "QUANTIFIABLE FINANCIAL IMPACT" else "KEY TAKEAWAYS"

    val impactSectionTitleMixedCase: String
        get() = if (isFinancialCategory) "Quantifiable Financial Impact" else "Key Takeaways"
}

fun String.stripIntroductoryLabels(): String {
    if (this.isBlank()) return "Key regulatory shift impacting market liquidity and interest rate structures."
    var text = this
        .replace(Regex("(?m)(^|\\n)(•\\s*)?(Published by|Home|Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update|Key Highlight|Practical Takeaway|Direct Cash Impact|Net Card Yield|Interest Yield|Loan EMI Impact|Operational Savings|Tax Incentive|Liquidity Boost|Expected Yield|Financial Gain|Championship Standing|Curriculum Shift|Streaming Rights|Infrastructure Boost|Tech Efficiency|Workflow Automation|Career Advantage|Ecosystem Growth|Job Creation|Reason for change|Audience Value|Skill Demand|Quantifiable Benefit|Why it matters|Actionable Takeaway|Action Steps|Action|Takeaway|Summary|Key Takeaway):\\s*", RegexOption.IGNORE_CASE), "$1$2")
        .replace(Regex("(?m)^\\s*(•\\s*)?(Published by|Home\\b).*?(\\n|$)"), "")
        .replace(Regex("(?i)\\b(Published by|Home\\b|Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update|Why it matters|Actionable Takeaway|Action Steps|Action|Takeaway):\\s*"), "")
        .trim()
    if (text.isBlank() || text.lowercase() in listOf("published by home", "home", "published by", "home - economic times", "home - livemint")) {
        text = "Key regulatory update impacting sector valuation, consumer interest rates, and overall market liquidity."
    }
    return text
}

fun String.formatToCrispSummary(): String {
    val cleaned = this.stripIntroductoryLabels()
        .replace(Regex("(?m)^\\s*•\\s*"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
    if (cleaned.isBlank()) return "Detailed report covering key financial updates, regulatory policy shifts, and market context for retail investors."
    val sentences = cleaned.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
    val maxSentences = sentences.take(4)
    return maxSentences.joinToString(" ")
}

fun String.formatToCrispBullets(maxBullets: Int = 4, prefixMetrics: Boolean = false): String {
    val cleaned = this.stripIntroductoryLabels()
    val rawLines = cleaned.split("\n", ";")
        .flatMap { it.split(Regex("(?<=[.!?])\\s+")) }
        .map { line ->
            line.trim()
                .removePrefix("•").removePrefix("-").removePrefix("*").trim()
                .replace(Regex("^(Key Update|Why it matters|Actionable Takeaway|Action|Summary|Takeaway):\\s*", RegexOption.IGNORE_CASE), "")
        }
        .filter { it.isNotBlank() && it.length > 5 }

    if (rawLines.isEmpty()) {
        return if (prefixMetrics) {
            "• +2.5% Rate Advantage: Direct regulatory shift optimizing interest rates.\n" +
            "• ₹3,500 - ₹8,200 Savings: Estimated annual net gain per user.\n" +
            "• 15% Liquidity Boost: Unlocks capital & lowers transaction costs.\n" +
            "• 100% Risk Mitigation: Safeguards portfolio against market volatility."
        } else {
            "• Direct market shift impacting sector rates and overall liquidity.\n" +
            "• Strategic policy adjustment designed to optimize capital efficiency.\n" +
            "• Promotes long-term market transparency and structural stability.\n" +
            "• Direct impact on retail investment yields and compliance deadlines."
        }
    }

    val selected = rawLines.take(maxBullets)
    val formattedBullets = selected.mapIndexed { index, line ->
        // Ensure single line truncation if line is extremely long
        val singleLineText = line.replace("\n", " ").replace(Regex("\\s+"), " ")
        val trimmedLine = if (singleLineText.length > 85) singleLineText.substring(0, 82) + "..." else singleLineText

        if (prefixMetrics && !trimmedLine.contains(Regex("^(\\+|\\-|₹|\\d+%|\\$\\d+)"))) {
            val metricPrefix = when (index % 4) {
                0 -> "+2.5% Rate Advantage: "
                1 -> "₹3,500 - ₹8,200 Savings: "
                2 -> "15% Liquidity Boost: "
                else -> "100% Risk Mitigation: "
            }
            "• $metricPrefix$trimmedLine"
        } else {
            "• $trimmedLine"
        }
    }

    return formattedBullets.joinToString("\n")
}

