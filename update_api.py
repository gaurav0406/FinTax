import re

with open("app/src/main/java/com/example/network/supabase/SupabaseApiService.kt", "r") as f:
    content = f.read()

# We need to import org.json.JSONObject to parse the JSON string
if "import org.json.JSONObject" not in content:
    content = content.replace("import retrofit2.http.Query", "import retrofit2.http.Query\nimport org.json.JSONObject\nimport org.json.JSONArray")

new_to_entity = """
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
            title = newsTitle,
            summaryWhatHappened = summaryWhatHappened ?: "Summary unavailable.",
            summaryWhoImpacted = summaryWhoImpacted ?: "Taxpayers, Investors & General Public",
            summaryActionableTakeaway = summaryActionableTakeaway ?: "Check official updates.",
            summaryText = summaryText ?: summaryWhatHappened ?: newsTitle,
            category = category ?: "Stock Market India",
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
"""

content = re.sub(r'fun toEntity\(\): FinancialNewsEntity\? \{.*?\n    \}', new_to_entity.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/example/network/supabase/SupabaseApiService.kt", "w") as f:
    f.write(content)

