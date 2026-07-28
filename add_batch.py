import re

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "r") as f:
    content = f.read()

batch_func = """    suspend fun summarizeNewsBatch(newsItems: List<Pair<String, String>>): Result<List<FinancialNewsEntity>> = withContext(Dispatchers.IO) {
        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext Result.success(newsItems.map { createFallbackEntity(it.first, it.second) })
        }

        try {
            val systemInstruction = \"\"\"
                You are an expert financial news summarizer. You will be provided with a JSON array of news items, each containing an id and rawText.
                Extract and structure the news into a JSON array of objects.
                All output MUST be in English.
                
                Respond ONLY with a JSON array of objects, each following this exact schema:
                {
                  "id": "The integer id of the news item provided in the input",
                  "summary": "Provide a detailed 7 to 8-line summary of the news in English. Do NOT include prefixes like 'What happened:'.",
                  "reason": "Provide 2 to 4 lines explaining why the government, entity, or individual has taken this decision/action in English. Do NOT include prefixes like 'Reason:'.",
                  "financial_impact": "What is the financial impact or the benefits users can gain in English? Use 2 to 3 lines. Use crisp, quantifiable numbers and bullet points.",
                  "action": "Provide actionable steps (2 to 3 lines) a user or company should take based on this news in English. Do NOT include prefixes like 'Actionable Takeaway:' or 'Action:'.",
                  "category": "One of: Stock Market India, ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"
                }
            \"\"\".trimIndent()

            val inputJsonArray = org.json.JSONArray()
            newsItems.forEachIndexed { index, pair ->
                val itemObj = org.json.JSONObject()
                itemObj.put("id", index)
                itemObj.put("rawText", pair.first)
                inputJsonArray.put(itemObj)
            }

            val prompt = inputJsonArray.toString()
            
            val textResponse = GeminiClient.generateContent(
                apiKey = apiKey,
                prompt = prompt,
                systemInstruction = systemInstruction,
                responseMimeType = "application/json"
            ) ?: ""
            
            val cleanedJson = textResponse.replace("```json", "").replace("```", "").trim()
            val resultArray = org.json.JSONArray(cleanedJson)
            
            val resultMap = mutableMapOf<Int, org.json.JSONObject>()
            for (i in 0 until resultArray.length()) {
                val obj = resultArray.getJSONObject(i)
                resultMap[obj.getInt("id")] = obj
            }
            
            val entities = newsItems.mapIndexed { index, pair ->
                val llmResult = resultMap[index]
                if (llmResult != null) {
                    FinancialNewsEntity(
                        title = "Key ${llmResult.optString("category", "Finance")} Update",
                        summaryWhatHappened = llmResult.optString("summary", ""),
                        summaryWhoImpacted = llmResult.optString("impacted_users", ""),
                        summaryActionableTakeaway = llmResult.optString("action", ""),
                        summaryText = llmResult.optString("reason", ""),
                        category = llmResult.optString("category", "ITR & Tax"),
                        financialActionUrl = pair.second,
                        sourceUrl = pair.second,
                        sourceName = "AI Summarized News",
                        financialImpactBullets = llmResult.optString("financial_impact", ""),
                        publishedAt = System.currentTimeMillis()
                    )
                } else {
                    createFallbackEntity(pair.first, pair.second)
                }
            }
            Result.success(entities)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.success(newsItems.map { createFallbackEntity(it.first, it.second) })
        }
    }

"""

# insert right before `fun generateFallbackImpact`
content = content.replace("    fun generateFallbackImpact", batch_func + "    fun generateFallbackImpact")

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "w") as f:
    f.write(content)
