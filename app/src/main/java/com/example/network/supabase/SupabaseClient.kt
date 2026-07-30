package com.example.network.supabase

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    fun getSupabaseUrl(): String {
        return try {
            val field = BuildConfig::class.java.getField("SUPABASE_URL")
            val url = field.get(null) as? String
            if (!url.isNullOrBlank() && !url.contains("your-project") && url.startsWith("http")) {
                if (!url.endsWith("/")) "$url/" else url
            } else null
        } catch (e: Exception) {
            null
        } ?: "https://frldttulizmyaqpmluqz.supabase.co/"
    }

    fun getSupabaseKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("SUPABASE_KEY")
            val key = field.get(null) as? String
            if (!key.isNullOrBlank() && !key.contains("YOUR_SUPABASE")) key else null
        } catch (e: Exception) {
            null
        } ?: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZybGR0dHVsaXpteWFxcG1sdXF6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc4NDgwNjkwMCwiZXhwIjoyMTAwNDgyOTAwfQ.cB-qICUgrVsbPfJTfuvTYMQcAda5y0eTLupBTsUQT5U"
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val key = getSupabaseKey()
        val newRequest = chain.request().newBuilder().apply {
            if (key.isNotBlank()) {
                addHeader("apikey", key)
                addHeader("Authorization", "Bearer $key")
            }
        }.build()
        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: SupabaseApiService by lazy {
        val baseUrl = getSupabaseUrl()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApiService::class.java)
    }
}

