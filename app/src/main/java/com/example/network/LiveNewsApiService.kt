package com.example.network

import com.example.data.FinancialNewsEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class ProcessedScrapedDataDto(
    val id: Int? = null,
    val title: String? = null,
    val url: String? = null,
    val text: String? = null,
    val category: String? = null,
    @Json(name = "sourceName") val sourceName: String? = null,
    val imageUrl: String? = null,
    @Json(name = "llm_summary") val llmSummary: LlmSummaryDto? = null
) {
    fun toEntity(): FinancialNewsEntity? {
        val newsTitle = title ?: return null
        val lowerTitle = newsTitle.lowercase()
        if (lowerTitle.contains("eerie") || lowerTitle.contains("shopping") || lowerTitle.contains("allure") || lowerTitle.contains("ghostly")) {
            return null
        }
        val newsUrl = url ?: return null
        return FinancialNewsEntity(
            title = newsTitle,
            summaryWhatHappened = llmSummary?.summary ?: text?.take(150) ?: "Summary unavailable.",
            summaryWhoImpacted = llmSummary?.whoImpacted ?: "Taxpayers, Investors & General Public",
            summaryActionableTakeaway = llmSummary?.action ?: "Check official updates.",
            summaryText = llmSummary?.reason ?: text ?: newsTitle,
            category = llmSummary?.category ?: category ?: "Financial News",
            sourceUrl = newsUrl,
            sourceName = sourceName ?: "Indian Financial Feed",
            imageUrl = imageUrl,
            financialImpactBullets = llmSummary?.financialImpact,
            publishedAt = System.currentTimeMillis()
        )
    }
}

@JsonClass(generateAdapter = true)
data class LlmSummaryDto(
    val summary: String? = null,
    @Json(name = "who_impacted") val whoImpacted: String? = null,
    val reason: String? = null,
    @Json(name = "financial_impact") val financialImpact: String? = null,
    val action: String? = null,
    val category: String? = null
)

interface LiveNewsApiService {
    @GET("backend_pipeline/processed_scraped_data.json")
    suspend fun getProcessedData(): List<ProcessedScrapedDataDto>
}

object LiveNewsClient {
    private const val BASE_URL = "https://raw.githubusercontent.com/gaurav0406/FinTax/main/"

    val apiService: LiveNewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LiveNewsApiService::class.java)
    }
}

