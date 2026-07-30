package com.example.network.supabase

import com.example.data.FinancialNewsEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query
import org.json.JSONObject
import org.json.JSONArray

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
    @Json(name = "financialActionUrl") val financialActionUrl: String? = null,
    @Json(name = "topicCluster") val topicCluster: String? = null
) {
    fun toEntity(): FinancialNewsEntity? {
        val newsTitle = title ?: return null
        val newsUrl = sourceUrl ?: return null
        
        var impactStr = financialImpactBullets
        var metricsStr: String? = null
        var jargonStr: String? = null
        
        try {
            if (financialImpactBullets != null && financialImpactBullets.startsWith("{")) {
                val json = JSONObject(financialImpactBullets)
                impactStr = json.optString("impact", null)
                
                val metricsArray = json.optJSONArray("metrics")
                if (metricsArray != null && metricsArray.length() > 0) {
                    val metricsList = mutableListOf<String>()
                    for (i in 0 until metricsArray.length()) {
                        metricsList.add(metricsArray.getString(i))
                    }
                    metricsStr = metricsList.joinToString("|||")
                }
                
                val jargonObj = json.optJSONObject("jargon")
                if (jargonObj != null && jargonObj.length() > 0) {
                    val jargonList = mutableListOf<String>()
                    val keys = jargonObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        jargonList.add("$key: ${jargonObj.getString(key)}")
                    }
                    jargonStr = jargonList.joinToString("|||")
                }
            }
        } catch (e: Exception) {
            // Not a valid JSON, just use it as string
        }

        return FinancialNewsEntity(
            id = id ?: 0,
            title = newsTitle,
            summaryWhatHappened = summaryWhatHappened ?: "Summary unavailable.",
            summaryWhoImpacted = summaryWhoImpacted ?: "Taxpayers, Investors & General Public",
            summaryActionableTakeaway = summaryActionableTakeaway ?: "Check official updates.",
            summaryText = summaryText ?: summaryWhatHappened ?: newsTitle,
            category = category ?: "Financial News",
            topicCluster = topicCluster ?: "Latest Updates",
            financialActionUrl = financialActionUrl,
            sourceUrl = newsUrl,
            sourceName = sourceName ?: "Indian Financial Feed",
            audioUrl = audioUrl,
            imageUrl = imageUrl,
            financialImpactBullets = impactStr,
            keyMetrics = metricsStr,
            jargonTerms = jargonStr,
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
        @Query("limit") limit: Int = 100,
        @Query("publishedAt") publishedAtFilter: String? = null
    ): List<SupabaseNewsDto>
}

