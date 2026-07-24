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
    // Replace this with your actual hosted backend URL
    private const val BASE_URL = "https://your-cloud-provider-url.com/"

    val apiService: LiveNewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(LiveNewsApiService::class.java)
    }
}
