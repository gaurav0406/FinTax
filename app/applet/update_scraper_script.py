import re

with open("backend_pipeline/news_scraper_cron.py", "r", encoding="utf-8") as f:
    code = f.read()

# 1. Update FEEDS for Credit Cards to strictly India region sources
old_feeds_pattern = r'FEEDS = \[\s*# Credit Card Sources[\s\S]*?# Financial News Sources'
new_feeds_code = '''FEEDS = [
    # Credit Card Sources (Strictly India Region)
    {
        "category": "Credit Cards",
        "url": "https://cardinsider.com/feed/",
        "sourceName": "Card Insider India"
    },
    {
        "category": "Credit Cards",
        "url": "https://cardexpert.in/feed/",
        "sourceName": "CardExpert India"
    },
    {
        "category": "Credit Cards",
        "url": "https://blog.bankbazaar.com/category/credit-cards/feed/",
        "sourceName": "BankBazaar Credit Cards"
    },
    {
        "category": "Credit Cards",
        "url": "https://technofino.in/community/blogs/index.rss",
        "sourceName": "TechnoFino India"
    },

    # Financial News Sources'''

code = re.sub(old_feeds_pattern, new_feeds_code, code)

# 2. Update detect_category to filter out non-India credit card content
old_detect_cat = r'def detect_category\(title: str, text: str, feed_category: str\) -> str:[\s\S]*?# 1\. Credit Cards'
new_detect_cat = '''def detect_category(title: str, text: str, feed_category: str) -> str:
    content = (title + " " + text).lower()
    
    # Check for foreign/US credit card content without India context
    non_india_keywords = [
        "chase sapphire", "delta sky", "united miles", "southwest rapid", 
        "capital one us", "american express us", "tsa precheck", "global entry us"
    ]
    if any(k in content for k in non_india_keywords) and not any(ik in content for ik in ["india", "hdfc", "sbi", "rbi", "rupay", "icici", "axis"]):
        return "Financial News"

    # 1. Credit Cards (India Region)'''

code = re.sub(old_detect_cat, new_detect_cat, code)

# 3. Update call_gemini_batch_api prompt
old_prompt_pattern = r'prompt = f"""You are an expert Financial Journalist[\s\S]*?Respond ONLY with a JSON Array of objects matching this exact format for each item:\n\['
new_prompt = '''prompt = f"""You are an expert Indian Financial Journalist and UX Content Architect.
Analyze these news items and produce structured, direct 6 to 7 line narrative summaries.

Output MUST be strictly valid JSON without markdown code blocks.

SUMMARY REQUIREMENTS:
1. Provide a direct, cohesive narrative summary paragraph that is EXACTLY 6 to 7 lines long (approx 90 to 130 words).
2. Internally incorporate answers to: (a) what happened & core context, (b) why it matters, (c) who is impacted, and (d) financial benefits or tangible value into the single narrative paragraph.
3. DO NOT output bullet points, section headers, or prefix labels like "User Impacted:", "Why It matters:", "Financial benefits:", "Key Update:", "Actionable Takeaway:", or section titles inside the text. Directly summarized text output MUST be visible.
4. For the "Credit Cards" category, ONLY focus on news relevant to the India region (e.g., Indian banks like HDFC, SBI Card, ICICI, Axis, RuPay, RBI rules, Indian lounge access).

Input Items:
{json.dumps(simplified, indent=2)}

Respond ONLY with a JSON Array of objects matching this exact format for each item:
['''

code = re.sub(old_prompt_pattern, new_prompt, code)

# Update the json array output schema in prompt
old_json_schema = r'"summary": "Provide a cohesive, high-readability 5 to 6 line narrative overview covering what happened and the core market context\.",\s*"who_impacted":[\s\S]*?"action": "Provide 1 crisp bullet point starting exactly with \'• Actionable Takeaway: \'"'
new_json_schema = '"summary": "Direct 6 to 7 line narrative summary paragraph seamlessly weaving what happened, why it matters, who is impacted, and financial benefits without any bullet labels or section titles."'

code = re.sub(old_json_schema, new_json_schema, code)

# 4. Update generate_fallback_llm_summary for 6-7 lines without labels
old_fallback_pattern = r'def generate_fallback_llm_summary\(item: dict\) -> dict:[\s\S]*?return \{\s*"id": item\["id"\]'
new_fallback = '''def generate_fallback_llm_summary(item: dict) -> dict:
    title = item["title"]
    category = item["category"]
    raw_text = item.get("text", title)
    
    clean_text = clean_html_text(raw_text)
    raw_sentences = [
        s.strip() for s in re.split(r'[.!?]+', clean_text)
        if len(s.strip()) > 15 and not re.search(r'(?i)\\b(published\\s+by|home\\b|copyright|all\\s+rights\\s+reserved|click\\s+here|read\\s+more)\\b', s)
    ]
    
    sentences = [s for s in raw_sentences if s.lower() not in title.lower()]
    if not sentences:
        sentences = [title]
        
    # Take 6 to 7 sentences to form a solid 6 to 7 line narrative summary
    summary_sentences = sentences[:7]
    summary = ". ".join(summary_sentences)
    summary = re.sub(r'\\.\\s*\\.', '.', summary)
    if not summary.endswith("."):
        summary += "."

    return {
        "id": item["id"]'''

code = re.sub(old_fallback_pattern, new_fallback, code)

# 5. Update fallback return object
old_fallback_return = r'"title": title\[:250\],\s*"summary": summary\[:1200\],\s*"who_impacted":[\s\S]*?"topic_cluster": f"\{category\} Update"'
new_fallback_return = '''"title": title[:250],
        "summary": summary[:1500],
        "who_impacted": "",
        "reason": "",
        "financial_impact": "",
        "action": "",
        "category": category,
        "topic_cluster": f"{category} Update"'''

code = re.sub(old_fallback_return, new_fallback_return, code)

# 6. Update push_to_supabase_rest to push clean summary narrative without labels
old_push_pattern = r'"summaryWhatHappened": llm\.get\("summary", ""\)\[:1000\],\s*"summaryWhoImpacted": llm\.get\("who_impacted", ""\)\[:500\],\s*"summaryActionableTakeaway": llm\.get\("action", ""\)\[:1000\],\s*"summaryText": llm\.get\("reason", ""\)\[:1500\],'
new_push_code = '"summaryWhatHappened": llm.get("summary", "")[:1500],\n            "summaryWhoImpacted": "",\n            "summaryActionableTakeaway": "",\n            "summaryText": llm.get("summary", "")[:1500],'

code = re.sub(old_push_pattern, new_push_code, code)

with open("backend_pipeline/news_scraper_cron.py", "w", encoding="utf-8") as f:
    f.write(code)

print("Updated news_scraper_cron.py successfully!")
