import re

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "r") as f:
    content = f.read()

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

# Since summarizeNews uses a single object and summarizeNewsBatch uses an array, I should handle them correctly. 
# Or wait, the NewsProcessorService's original summarizeNews asks for a single object. I will just replace both with the new JSON structure.
# But for single item, it shouldn't expect an array. Let's just create two variables.

single_update = system_instruction_update.replace("Respond ONLY with a JSON Array of objects matching this exact format for each item:\n[\n  {", "Respond ONLY with a JSON object matching this exact format:\n{")
single_update = single_update.replace("  }\n]", "}")

# Use generic regex to replace the content of the two string blocks
content = re.sub(
    r'val systemInstruction = """\n\s*You are an expert financial news summarizer\. Extract and structure.*?\}"""\.trimIndent\(\)',
    f'val systemInstruction = """{single_update}""".trimIndent()',
    content,
    flags=re.DOTALL
)

content = re.sub(
    r'val systemInstruction = """\n\s*You are an expert financial news summarizer\. You will be provided.*?\}"""\.trimIndent\(\)',
    f'val systemInstruction = """{system_instruction_update}""".trimIndent()',
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "w") as f:
    f.write(content)
