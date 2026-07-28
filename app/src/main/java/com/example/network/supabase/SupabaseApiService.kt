package com.example.network.supabase

import com.example.data.FinancialNewsEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class SupabaseNewsDto(
    val id: Int? = null,
    val title: String? = null,
    @Json(name = "sourceUrl") val sourceUrl: String? = null,
    @Json(name = "summaryWhatHappened") val summaryWhatHappened: String? = null,
    @Json(name = "summaryWhoImpacted") val summaryWhoImpacted: String? = null,
    @Json(name = "summaryText") val summaryText: String? = null,
    @Json(name = "summaryActionableTakeaway") val summaryActionableTakeaway: String? = null,
    @Json(name = "financialImpactBullets") val financialImpactBullets: String? = null,
    val category: String? = null,
    @Json(name = "sourceName") val sourceName: String? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "publishedAt") val publishedAt: Long? = null,
    @Json(name = "audioUrl") val audioUrl: String? = null,
    @Json(name = "financialActionUrl") val financialActionUrl: String? = null
) {
    fun toEntity(): FinancialNewsEntity? {
        val newsTitle = title ?: return null
        val newsUrl = sourceUrl ?: return null
        return FinancialNewsEntity(
            title = newsTitle,
            summaryWhatHappened = summaryWhatHappened ?: "Summary unavailable.",
            summaryWhoImpacted = summaryWhoImpacted ?: "Taxpayers, Investors & General Public",
            summaryActionableTakeaway = summaryActionableTakeaway ?: "Check official updates.",
            summaryText = summaryText ?: summaryWhatHappened ?: newsTitle,
            category = category ?: "Stock Market India",
            financialActionUrl = financialActionUrl,
            sourceUrl = newsUrl,
            sourceName = sourceName ?: "Indian Financial Feed",
            audioUrl = audioUrl,
            imageUrl = imageUrl,
            financialImpactBullets = financialImpactBullets,
            publishedAt = publishedAt ?: System.currentTimeMillis()
        )
    }
}

interface SupabaseApiService {
    // Fetches news from a Supabase PostgreSQL table (e.g., 'financial_news')
    @GET("rest/v1/financial_news")
    suspend fun getLiveNews(
        @Query("select") select: String = "*",
        @Query("order") order: String = "publishedAt.desc",
        @Query("limit") limit: Int = 100
    ): List<SupabaseNewsDto>
}

