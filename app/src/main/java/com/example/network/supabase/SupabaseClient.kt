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
    // These should be configured in your .env or secrets panel and exposed via BuildConfig
    // e.g., BuildConfig.SUPABASE_URL
    private const val BASE_URL = "https://your-project.supabase.co/" 

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        // Retrieve Supabase Key (anon key) securely from environment/secrets
        // val supabaseKey = BuildConfig.SUPABASE_KEY
        val supabaseKey = "YOUR_SUPABASE_ANON_KEY_HERE"
        
        val newRequest = chain.request().newBuilder()
            .addHeader("apikey", supabaseKey)
            .addHeader("Authorization", "Bearer \$supabaseKey")
            .build()
        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApiService::class.java)
    }
}
