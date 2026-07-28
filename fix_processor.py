import re

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "r") as f:
    content = f.read()

new_prompt_logic = """                val systemInstruction = \"\"\"
                    You are an expert financial news summarizer. Extract and structure the news into this exact JSON format. Keep it concise, insightful, and actionable. All output MUST be in English.
                    
                    Respond ONLY with JSON:
                    {
                      "summary": "Provide a detailed 7 to 8-line summary of the news in English. Do NOT include prefixes like 'What happened:'.",
                      "reason": "Provide 2 to 4 lines explaining why the government, entity, or individual has taken this decision/action in English. Do NOT include prefixes like 'Reason:'.",
                      "financial_impact": "What is the financial impact or the benefits users can gain in English? Use 2 to 3 lines. Use crisp, quantifiable numbers and bullet points.",
                      "action": "Provide actionable steps (2 to 3 lines) a user or company should take based on this news in English. Do NOT include prefixes like 'Actionable Takeaway:' or 'Action:'.",
                      "category": "One of: Stock Market India, ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"
                    }
                \"\"\".trimIndent()

                val prompt = "News: $rawText"
                
                val textResponse = GeminiClient.generateContent(
                    apiKey = apiKey,
                    prompt = prompt,
                    systemInstruction = systemInstruction,
                    responseMimeType = "application/json"
                ) ?: ""
"""

content = re.sub(r'                val prompt = """(.*?)""".trimIndent\(\)\s*val textResponse = GeminiClient\.generateContent\(apiKey, prompt\) \?: ""', new_prompt_logic, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "w") as f:
    f.write(content)
