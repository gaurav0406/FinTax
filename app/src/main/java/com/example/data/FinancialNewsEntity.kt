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
    val category: String, // 'Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy'
    val financialActionUrl: String? = null,
    val sourceUrl: String,
    val sourceName: String = "Indian Financial Feed",
    val audioUrl: String? = null,
    val imageUrl: String? = null,
    val financialImpactBullets: String? = null,
    val publishedAt: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false,
    val readCount: Int = 1250,
    val shareCount: Int = 180
) {
    val commentCount: Int
        get() = (id * 17) % 150 + 5

    val isFinancialCategory: Boolean
        get() = when (category.trim()) {
            "Stock Market India", "ITR & Tax", "Credit Cards", "Loans & FDs", "Markets & Mutual Funds",
            "RBI & Policy", "FinTech & Crypto", "Smart Investing", "Personal Finance",
            "Cars & EV", "Video Shorts" -> true
            else -> false
        }

    val impactSectionTitle: String
        get() = if (isFinancialCategory) "QUANTIFIABLE FINANCIAL IMPACT" else "KEY TAKEAWAYS"

    val impactSectionTitleMixedCase: String
        get() = if (isFinancialCategory) "Quantifiable Financial Impact" else "Key Takeaways"
}
