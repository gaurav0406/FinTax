package com.example.network

import com.example.BuildConfig
import com.example.data.FinancialNewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiNewsService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
You are an expert Indian Financial NLP & Tax Journalist.
Analyze raw news or tax text and return a structured JSON output strictly conforming to this schema:

{
  "title": "Catchy headline tailored for Indian taxpayers/investors (Max 10 words)",
  "summary": [
    "Point 1: What happened (1-2 concise sentences)",
    "Point 2: Who is impacted e.g. Salaried Class, Senior Citizens, Taxpayers (1 sentence)",
    "Point 3: Actionable Takeaway e.g. File ITR-1 before July 31, Link Aadhaar (1 sentence)"
  ],
  "category": "Must be EXACTLY ONE of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy']",
  "financial_action_url": "Optional affiliate or official portal URL (e.g. incometax.gov.in, rbi.org.in, or null)",
  "source_url": "Original article link"
}

Constraints:
1. 'title' must be maximum 10 words.
2. 'summary' must be exactly 3 bullet points. Total words across all 3 points must be under 60 words.
3. 'category' must strictly match one of the 5 allowed strings.
"""

    suspend fun summarizeNewsWithGemini(rawText: String, sourceUrl: String = "https://eportal.incometax.gov.in"): Result<FinancialNewsEntity> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Fallback rule if key is blank or placeholder
            return@withContext Result.success(createFallbackEntity(rawText, sourceUrl))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val promptText = "Source URL: $sourceUrl\nRaw Article Text:\n$rawText"

            val jsonPayload = JSONObject().apply {
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", SYSTEM_PROMPT)))
                })
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", promptText)))
                }))
                put("generationConfig", JSONObject().apply {
                    put("response_mime_type", "application/json")
                    put("temperature", 0.2)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.success(createFallbackEntity(rawText, sourceUrl))
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textContent = parts?.optJSONObject(0)?.optString("text") ?: ""

            val parsedJson = JSONObject(textContent)
            val title = parsedJson.optString("title", "Indian Tax & Financial News Update")
            val summaryArray = parsedJson.optJSONArray("summary")
            
            val p1 = summaryArray?.optString(0) ?: "What Happened: Important financial update."
            val p2 = summaryArray?.optString(1) ?: "Who is Impacted: Salaried taxpayers & investors."
            val p3 = summaryArray?.optString(2) ?: "Actionable Takeaway: Verify guidelines on the official portal."

            val category = parsedJson.optString("category", "ITR & Tax")
            val actionUrl = if (parsedJson.has("financial_action_url") && !parsedJson.isNull("financial_action_url")) {
                parsedJson.getString("financial_action_url")
            } else null

            val summaryText = "$p1 $p2 $p3"
            val trimmedSummary = if (summaryText.split(" ").size > 60) {
                summaryText.split(" ").take(60).joinToString(" ")
            } else summaryText

            val entity = FinancialNewsEntity(
                title = title,
                summaryWhatHappened = p1,
                summaryWhoImpacted = p2,
                summaryActionableTakeaway = p3,
                summaryText = trimmedSummary,
                category = category,
                financialActionUrl = actionUrl,
                sourceUrl = sourceUrl,
                sourceName = "Gemini AI Processed Feed",
                publishedAt = System.currentTimeMillis()
            )

            Result.success(entity)
        } catch (e: Exception) {
            Result.success(createFallbackEntity(rawText, sourceUrl))
        }
    }

    private fun createFallbackEntity(rawText: String, sourceUrl: String): FinancialNewsEntity {
        val snippet = rawText.take(150).replace("\n", " ")
        val category = when {
            rawText.contains("credit card", true) || rawText.contains("reward", true) -> "Credit Cards"
            rawText.contains("fd", true) || rawText.contains("loan", true) || rawText.contains("interest", true) -> "Loans & FDs"
            rawText.contains("mutual fund", true) || rawText.contains("market", true) || rawText.contains("sip", true) -> "Markets & Mutual Funds"
            rawText.contains("rbi", true) || rawText.contains("repo rate", true) || rawText.contains("policy", true) -> "RBI & Policy"
            else -> "ITR & Tax"
        }

        val actionUrl = when (category) {
            "ITR & Tax" -> "https://eportal.incometax.gov.in"
            "Credit Cards" -> "https://www.sbicard.com"
            "RBI & Policy" -> "https://www.rbi.org.in"
            else -> "https://www.moneycontrol.com"
        }

        val p1 = "What Happened: New guidelines announced for $category regarding $snippet..."
        val p2 = "Who is Impacted: Salaried individuals, individual taxpayers, and retail investors."
        val p3 = "Actionable Takeaway: Review official portal notices before the next tax quarter deadline."

        val summaryText = "$p1 $p2 $p3"

        return FinancialNewsEntity(
            title = "Key $category Update for Indian Taxpayers",
            summaryWhatHappened = p1,
            summaryWhoImpacted = p2,
            summaryActionableTakeaway = p3,
            summaryText = summaryText,
            category = category,
            financialActionUrl = actionUrl,
            sourceUrl = sourceUrl,
            sourceName = "Indian Financial News Feed",
            publishedAt = System.currentTimeMillis()
        )
    }
}
