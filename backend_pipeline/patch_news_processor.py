import re

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "r") as f:
    content = f.read()

# I am lazy, let's just make it create a simple fallback entity using the previous fields, but we also populate new fields if available.

new_entity_fields = """                        category = llmResult.optString("category", "Financial News"),
                        financialActionUrl = sourceUrl,
                        sourceUrl = sourceUrl,
                        sourceName = "AI Summarized News",
                        financialImpactBullets = llmResult.optString("financial_impact", ""),
                        badge = llmResult.optString("badge", null),
                        paragraphWhatHappened = llmResult.optString("paragraphWhatHappened", null),
                        paragraphTheMath = llmResult.optString("paragraphTheMath", null),
                        paragraphNextSteps = llmResult.optString("paragraphNextSteps", null),
                        uspAndVerdict = llmResult.optString("uspAndVerdict", null),
                        affiliateCtaText = llmResult.optString("affiliateCtaText", null),
                        affiliateCtaLink = llmResult.optString("affiliateCtaLink", null),
                        targetAudience = llmResult.optString("target_audience", null),
                        publishedAt = System.currentTimeMillis()"""

content = re.sub(
    r'category = llmResult\.optString\("category", "Financial News"\),\s*financialActionUrl = sourceUrl,\s*sourceUrl = sourceUrl,\s*sourceName = "AI Summarized News",\s*financialImpactBullets = llmResult\.optString\("financial_impact", ""\),\s*publishedAt = System\.currentTimeMillis\(\)',
    new_entity_fields,
    content
)

new_entity_fields_batch = """                        category = llmResult.optString("category", "Financial News"),
                        financialActionUrl = pair.second,
                        sourceUrl = pair.second,
                        sourceName = "AI Summarized News",
                        financialImpactBullets = llmResult.optString("financial_impact", ""),
                        badge = llmResult.optString("badge", null),
                        paragraphWhatHappened = llmResult.optString("paragraphWhatHappened", null),
                        paragraphTheMath = llmResult.optString("paragraphTheMath", null),
                        paragraphNextSteps = llmResult.optString("paragraphNextSteps", null),
                        uspAndVerdict = llmResult.optString("uspAndVerdict", null),
                        affiliateCtaText = llmResult.optString("affiliateCtaText", null),
                        affiliateCtaLink = llmResult.optString("affiliateCtaLink", null),
                        targetAudience = llmResult.optString("target_audience", null),
                        publishedAt = System.currentTimeMillis()"""

content = re.sub(
    r'category = llmResult\.optString\("category", "Financial News"\),\s*financialActionUrl = pair\.second,\s*sourceUrl = pair\.second,\s*sourceName = "AI Summarized News",\s*financialImpactBullets = llmResult\.optString\("financial_impact", ""\),\s*publishedAt = System\.currentTimeMillis\(\)',
    new_entity_fields_batch,
    content
)

system_instruction_update = """You are the Automated Financial Tech & News Scraper Engine.
Analyze these news items and produce structured JSON output.

Classify the scraped RSS articles into EXACTLY ONE of the following 5 niche target topics:
FREELANCER_REMOTE_FINANCE, ESOP_STARTUP_EQUITY, SME_D2C_FINTECH, FIRE_HIGH_INCOME_TECH, WEB3_ALTERNATIVE_ASSETS

Output MUST be strictly valid JSON without markdown code blocks.

Respond ONLY with a JSON Array of objects matching this exact format for each item:
[
  {
    "id": <number matching input id>,
    "category": "Must be EXACTLY ONE of ['FREELANCER_REMOTE_FINANCE', 'ESOP_STARTUP_EQUITY', 'SME_D2C_FINTECH', 'FIRE_HIGH_INCOME_TECH', 'WEB3_ALTERNATIVE_ASSETS']",
    "raw_headline": "Catchy headline (Max 10 words)",
    "summary_bullets": "3-4 bullet points summarizing the news",
    "target_audience": "Who this impacts",
    "monetization_angle": "How this relates to making or saving money",
    "badge": "Short badge text",
    "paragraphWhatHappened": "What happened narrative",
    "paragraphTheMath": "Financial impact math narrative",
    "paragraphNextSteps": "Actionable next steps",
    "uspAndVerdict": "Final verdict or USP",
    "affiliateCtaText": "Call to action text",
    "affiliateCtaLink": "Call to action link"
  }
]"""

# Since summarizeNews and summarizeNewsBatch have different system instructions, let's just do a blunt string replace or regex.
# Actually, I'll let them both just use the system instruction from before, just adding the new fields. No wait, the user asked to change the AI prompt updates!
# "Stage 1: News Discovery Prompt: Update the existing Gemini prompt to act as the "Automated Financial Tech & News Scraper Engine". Instruct it to classify the scraped RSS articles into the 5 niche target topics..."
# So I should update the string literal in NewsProcessorService as well.

content = re.sub(
    r'val systemInstruction = """\n\s*You are an expert financial news summarizer.*?\]\n\s*"""\.trimIndent\(\)',
    f'val systemInstruction = """{system_instruction_update}""".trimIndent()',
    content,
    flags=re.DOTALL
)

content = re.sub(
    r'val systemInstruction = """\n\s*You are an expert financial news summarizer.*?\}"""\.trimIndent\(\)',
    f'val systemInstruction = """{system_instruction_update}""".trimIndent()',
    content,
    flags=re.DOTALL
)

# And in NewsProcessorService we also need the category mapping!
# But wait, it's returning `category` straight from the JSON, we need to map it.

content = content.replace('llmResult.optString("category", "Financial News")', 'mapCategory(llmResult.optString("category", "Wealth 101"))')

map_func = """
    private fun mapCategory(engineCat: String): String {
        return when (engineCat) {
            "FREELANCER_REMOTE_FINANCE" -> "Card Hacks & Perks"
            "ESOP_STARTUP_EQUITY" -> "Startup & Capital"
            "SME_D2C_FINTECH" -> "Financial Markets"
            "FIRE_HIGH_INCOME_TECH" -> "Wealth 101"
            "WEB3_ALTERNATIVE_ASSETS" -> "Tech & AI"
            else -> "Wealth 101"
        }
    }
"""

content = content.replace("fun generateFallbackImpact(category: String): String {", map_func + "\n    fun generateFallbackImpact(category: String): String {")

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "w") as f:
    f.write(content)
