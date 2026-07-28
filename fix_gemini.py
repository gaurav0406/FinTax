import re

with open("app/src/main/java/com/example/network/GeminiApiService.kt", "r") as f:
    content = f.read()

# Replace generateContent signature and url
new_signature = """    suspend fun generateContent(
        apiKey: String,
        prompt: String,
        systemInstruction: String? = null,
        responseMimeType: String? = null
    ): String? = withContext(Dispatchers.IO) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
        
        val part = JSONObject().put("text", prompt)
        val content = JSONObject().put("parts", JSONArray().put(part))
        val bodyJson = JSONObject().put("contents", JSONArray().put(content))
        
        if (systemInstruction != null) {
            val sysPart = JSONObject().put("text", systemInstruction)
            val sysContent = JSONObject().put("parts", JSONArray().put(sysPart))
            bodyJson.put("system_instruction", sysContent)
        }
        
        if (responseMimeType != null) {
            val genConfig = JSONObject().put("response_mime_type", responseMimeType)
            bodyJson.put("generation_config", genConfig)
        }"""

content = re.sub(r'    suspend fun generateContent\(apiKey: String, prompt: String\): String\? = withContext\(Dispatchers\.IO\) \{.*?val bodyJson = JSONObject\(\)\.put\("contents", JSONArray\(\)\.put\(content\)\)', new_signature, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/network/GeminiApiService.kt", "w") as f:
    f.write(content)
