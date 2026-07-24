package com.example.network.supabase

import com.example.data.FinancialNewsEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface SupabaseApiService {
    // Fetches news from a Supabase PostgreSQL table (e.g., 'financial_news')
    // The query can include parameters based on PostgREST syntax
    @GET("rest/v1/financial_news")
    suspend fun getLiveNews(
        @Query("select") select: String = "*",
        @Query("order") order: String = "publishedAt.desc",
        @Query("limit") limit: Int = 100
    ): List<FinancialNewsEntity>
}
