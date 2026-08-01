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
    val shareCount: Int = 180,
    val badge: String? = null,
    val paragraphWhatHappened: String? = null,
    val paragraphTheMath: String? = null,
    val paragraphNextSteps: String? = null,
    val uspAndVerdict: String? = null,
    val affiliateCtaText: String? = null,
    val affiliateCtaLink: String? = null,
    val targetAudience: String? = null,
    val communityTweetHandle: String? = null,
    val communityTweetName: String? = null,
    val communityTweetText: String? = null,
    val communitySentimentBadge: String? = null
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
    if (this.isBlank()) return ""
    var text = this
        .replace(Regex("(?m)(^|\\n)(•\\s*)?(User Impacted|Why It matters|Why it matters|Financial benefits|Financial Impact|Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update|Key Highlight|Practical Takeaway|Direct Cash Impact|Net Card Yield|Interest Yield|Loan EMI Impact|Operational Savings|Tax Incentive|Liquidity Boost|Expected Yield|Financial Gain|Quantifiable Benefit|Actionable Takeaway|Action Steps|Action|Takeaway|Summary|Key Takeaway):\\s*", RegexOption.IGNORE_CASE), "$1")
        .replace(Regex("(?m)^\\s*(•\\s*)?(Published by|Home\\b).*?(\\n|$)"), "")
        .replace(Regex("(?i)\\b(Published by|Home\\b|User Impacted|Why It matters|Why it matters|Financial benefits|Financial Impact|Actionable Takeaway|Action Steps|Action|Takeaway):\\s*"), "")
        .trim()
    if (text.isBlank() || text.lowercase() in listOf("published by home", "home", "published by", "home - economic times", "home - livemint")) {
        text = ""
    }
    return text
}

fun FinancialNewsEntity.getMergedOverview(): String {
    val primaryText = summaryWhatHappened.ifBlank { summaryText }
    val clean = primaryText.stripIntroductoryLabels()
    if (clean.isNotBlank()) return clean
    return title
}

fun FinancialNewsEntity.getMergedKeyTakeaways(): String {
    val overview = getMergedOverview()
    
    val rawList = mutableListOf<String>()
    
    val who = summaryWhoImpacted.stripIntroductoryLabels()
    if (who.isNotBlank()) rawList.add(who)
    
    val action = summaryActionableTakeaway.stripIntroductoryLabels()
    if (action.isNotBlank()) rawList.add(action)

    val impact = financialImpactBullets?.stripIntroductoryLabels() ?: ""
    if (impact.isNotBlank()) rawList.add(impact)

    if (summaryText.isNotBlank() && summaryText != summaryWhatHappened) {
        val why = summaryText.stripIntroductoryLabels()
        if (why.isNotBlank()) rawList.add(why)
    }

    val uniqueItems = rawList
        .flatMap { item -> item.split("\n", ";") }
        .map { line ->
            line.trim()
                .removePrefix("•").removePrefix("-").removePrefix("*").trim()
                .replace(Regex("^(User Impacted|Why It matters|Why it matters|Financial benefits|Financial Impact|Key Update|Market Context|Investor Takeaway|Actionable Takeaway|Action|Summary|Takeaway):\\s*", RegexOption.IGNORE_CASE), "")
        }
        .filter { it.isNotBlank() && it.length > 8 }
        .distinct()

    if (uniqueItems.isNotEmpty()) {
        return uniqueItems.take(4).joinToString("\n") { "• $it" }
    }

    // Fallback: If separate fields are empty, extract clean key takeaway bullet points from narrative text
    val textToSplit = if (summaryWhatHappened.length > 80) summaryWhatHappened else summaryText
    val sentences = textToSplit.stripIntroductoryLabels()
        .split(Regex("(?<=[.!?])\\s+"))
        .map { line ->
            line.trim()
                .removePrefix("•").removePrefix("-").removePrefix("*").trim()
                .replace(Regex("^(User Impacted|Why It matters|Why it matters|Financial benefits|Financial Impact|Key Update|Market Context|Investor Takeaway|Actionable Takeaway|Action|Summary|Takeaway):\\s*", RegexOption.IGNORE_CASE), "")
        }
        .filter { it.isNotBlank() && it.length > 12 }
        .distinct()

    if (sentences.size >= 2) {
        val takeawaySentences = if (sentences.size > 2) sentences.drop(1).take(3) else sentences.take(2)
        return takeawaySentences.joinToString("\n") { "• $it" }
    } else if (sentences.isNotEmpty()) {
        return "• ${sentences.first()}"
    }

    return ""
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

