package com.example.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    @Json(name = "items") val items: List<YouTubeVideoItem>?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoItem(
    @Json(name = "id") val id: YouTubeVideoId?,
    @Json(name = "snippet") val snippet: YouTubeVideoSnippet?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoId(
    @Json(name = "videoId") val videoId: String?
)

@JsonClass(generateAdapter = true)
data class YouTubeVideoSnippet(
    @Json(name = "title") val title: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "channelTitle") val channelTitle: String?,
    @Json(name = "publishedAt") val publishedAt: String?,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnails?
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    @Json(name = "high") val high: YouTubeThumbnailDetails?
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnailDetails(
    @Json(name = "url") val url: String?
)

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("videoDuration") videoDuration: String = "short", // for shorts
        @Query("maxResults") maxResults: Int = 15,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}

object YouTubeClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val apiService: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YouTubeApiService::class.java)
    }
}
