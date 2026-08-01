import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

prompt_replacement = """You are the Automated Financial Tech & News Scraper Engine.
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
]
"""

content = re.sub(
    r'prompt = f"""You are an expert Indian Financial Journalist.*?\]\n"""',
    f'prompt = f"""{prompt_replacement}"""',
    content,
    flags=re.DOTALL
)

mapping_code = """
def map_category(engine_cat: str) -> str:
    mapping = {
        "FREELANCER_REMOTE_FINANCE": "Card Hacks & Perks",
        "ESOP_STARTUP_EQUITY": "Startup & Capital",
        "SME_D2C_FINTECH": "Financial Markets",
        "FIRE_HIGH_INCOME_TECH": "Wealth 101",
        "WEB3_ALTERNATIVE_ASSETS": "Tech & AI"
    }
    return mapping.get(engine_cat, "Wealth 101")
"""

# inject mapping code
content = content.replace("def clean_html_text(text: str) -> str:", mapping_code + "\ndef clean_html_text(text: str) -> str:")

# update parsing loop
content = content.replace('summary_obj.get("topic_cluster", "Latest Updates")', 'summary_obj.get("monetization_angle", "")')

process_loop_old = """
        processed_item = {
            "id": item["id"],
            "title": summary_obj.get("title", item["title"])[:250],
            "url": item["url"],
            "text": item["text"],
            "category": cat,
            "sourceName": item["sourceName"],
            "imageUrl": item.get("imageUrl"),
            "llm_summary": {
                "summary": summary_obj.get("summary", ""),
                "who_impacted": summary_obj.get("who_impacted", ""),
                "reason": summary_obj.get("reason", ""),
                "financial_impact": summary_obj.get("financial_impact", ""),
                "action": summary_obj.get("action", ""),
                "category": cat,
                "topic_cluster": summary_obj.get("topic_cluster", "Latest Updates")
            }
        }
"""
process_loop_new = """
        app_category = map_category(summary_obj.get("category", ""))
        processed_item = {
            "id": item["id"],
            "title": summary_obj.get("raw_headline", item["title"])[:250],
            "url": item["url"],
            "text": item["text"],
            "category": app_category,
            "sourceName": item["sourceName"],
            "imageUrl": item.get("imageUrl"),
            "llm_summary": {
                "summary": summary_obj.get("paragraphWhatHappened", ""),
                "who_impacted": summary_obj.get("target_audience", ""),
                "reason": summary_obj.get("paragraphTheMath", ""),
                "financial_impact": summary_obj.get("monetization_angle", ""),
                "action": summary_obj.get("paragraphNextSteps", ""),
                "category": app_category,
                "topic_cluster": summary_obj.get("badge", "Latest Updates"),
                
                "badge": summary_obj.get("badge", ""),
                "paragraphWhatHappened": summary_obj.get("paragraphWhatHappened", ""),
                "paragraphTheMath": summary_obj.get("paragraphTheMath", ""),
                "paragraphNextSteps": summary_obj.get("paragraphNextSteps", ""),
                "uspAndVerdict": summary_obj.get("uspAndVerdict", ""),
                "affiliateCtaText": summary_obj.get("affiliateCtaText", ""),
                "affiliateCtaLink": summary_obj.get("affiliateCtaLink", ""),
                "targetAudience": summary_obj.get("target_audience", "")
            }
        }
"""
content = content.replace(process_loop_old, process_loop_new)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
