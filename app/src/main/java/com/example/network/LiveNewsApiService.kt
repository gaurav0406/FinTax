package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class LiveNewsDto(
    val title: String? = null,
    val summary: List<String>? = null,
    @Json(name = "summary_text") val summaryText: String? = null,
    val category: String? = null,
    @Json(name = "financial_action_url") val financialActionUrl: String? = null,
    @Json(name = "source_url") val sourceUrl: String? = null,
    @Json(name = "source_name") val sourceName: String? = null,
    @Json(name = "audio_url") val audioUrl: String? = null
)

interface LiveNewsApiService {
    @GET("live_news.json")
    suspend fun getLiveNews(): List<LiveNewsDto>
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
