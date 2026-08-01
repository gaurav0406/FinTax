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

single_update = system_instruction_update.replace("Respond ONLY with a JSON Array of objects matching this exact format for each item:\n[\n  {", "Respond ONLY with a JSON object matching this exact format:\n{").replace("  }\n]", "}")

start_marker_1 = 'val systemInstruction = """\n                    You are an expert financial news summarizer. Extract and structure'
end_marker_1 = '                    }\n                """.trimIndent()'
start_idx_1 = content.find('val systemInstruction = """\n                    You are an expert financial news summarizer. Extract')
end_idx_1 = content.find('""".trimIndent()', start_idx_1) + len('""".trimIndent()')

if start_idx_1 != -1 and end_idx_1 != -1:
    content = content[:start_idx_1] + f'val systemInstruction = """{single_update}""".trimIndent()' + content[end_idx_1:]

start_idx_2 = content.find('val systemInstruction = """\n                You are an expert financial news summarizer. You will be provided')
end_idx_2 = content.find('""".trimIndent()', start_idx_2) + len('""".trimIndent()')

if start_idx_2 != -1 and end_idx_2 != -1:
    content = content[:start_idx_2] + f'val systemInstruction = """{system_instruction_update}""".trimIndent()' + content[end_idx_2:]

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "w") as f:
    f.write(content)
