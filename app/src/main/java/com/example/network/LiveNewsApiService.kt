package com.example.network

import com.example.data.FinancialNewsEntity
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface LiveNewsApiService {
    @GET("live_news.json") // Replace with your actual endpoint
    suspend fun getLiveNews(): List<FinancialNewsEntity>
}

object LiveNewsClient {
    // If you are generating a live_news.json file and committing it to your GitHub repo,
    // you must use the "raw.githubusercontent.com" URL, NOT the normal github.com URL.
    // Retrofit base URLs MUST also end with a trailing slash.
    private const val BASE_URL = "https://raw.githubusercontent.com/gaurav0406/FinTax/main/"

    val apiService: LiveNewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LiveNewsApiService::class.java)
    }
}
