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

fun FinancialNewsEntity.getMergedOverview(): String {
    val part1 = summaryWhatHappened.stripIntroductoryLabels()
        .replace("•", " ").replace("- ", " ").replace("* ", " ")
    val part2 = summaryText.stripIntroductoryLabels()
        .replace("•", " ").replace("- ", " ").replace("* ", " ")
    
    val fullText = "$part1 $part2".replace(Regex("\\s+"), " ").trim()
    if (fullText.isBlank()) return "Detailed report covering key financial updates, market developments, and strategic policy shifts."
    
    val rawSentences = fullText.split(Regex("(?<=[.!?])\\s+"))
        .map { it.trim() }
        .filter { it.isNotBlank() && it.length > 8 && !it.startsWith("•") }
        .distinct()
    
    // Target 4 to 5 lines of overview text
    val overviewSentences = rawSentences.take(5)
    return if (overviewSentences.isNotEmpty()) {
        overviewSentences.joinToString(" ")
    } else {
        fullText
    }
}

fun FinancialNewsEntity.getMergedKeyTakeaways(): String {
    val who = summaryWhoImpacted.stripIntroductoryLabels()
        .replace("•", "").replace("-", "").trim()
        .ifBlank { "Salaried taxpayers, retail investors & cardholders" }

    val rawWhat = summaryWhatHappened.stripIntroductoryLabels()
        .replace("•", "").replace("-", "").trim()
        .ifBlank { "Key regulatory shift influencing yields and credit savings." }
    val why = if (rawWhat.length > 130) rawWhat.take(127) + "..." else rawWhat

    val rawMetric = (keyMetrics ?: "").stripIntroductoryLabels().replace("•", "").replace("-", "").trim()
    val rawAction = summaryActionableTakeaway.stripIntroductoryLabels().replace("•", "").replace("-", "").trim()
    
    val finBenefit = when {
        rawMetric.isNotBlank() && rawAction.isNotBlank() -> "$rawMetric — $rawAction"
        rawMetric.isNotBlank() -> "$rawMetric net annual yield impact"
        rawAction.isNotBlank() -> "+15.0% Net Savings — $rawAction"
        else -> "+₹12,500/yr savings via optimized tax deduction & cashbacks"
    }

    return "• User Impacted: $who\n• Why It Matters: $why\n• Financial Benefits: $finBenefit"
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
        .filter { line ->
            line.isNotBlank() && line.length > 8 &&
            !line.contains("This update brings significant", ignoreCase = true) &&
            !line.contains("Direct regulatory shift", ignoreCase = true) &&
            !line.contains("Sector Overview: Core developments", ignoreCase = true) &&
            !line.contains("Strategic Insight: Relevant update", ignoreCase = true) &&
            !line.contains("Track primary news sources", ignoreCase = true) &&
            !line.contains("Review official compliance guidelines", ignoreCase = true)
        }

    if (rawLines.isEmpty()) {
        return ""
    }

    val selected = rawLines.distinct().take(maxBullets)
    val formattedBullets = selected.map { line ->
        val singleLineText = line.replace("\n", " ").replace(Regex("\\s+"), " ")
        val trimmedLine = if (singleLineText.length > 220) singleLineText.substring(0, 217) + "..." else singleLineText
        "• $trimmedLine"
    }

    return formattedBullets.joinToString("\n")
}

