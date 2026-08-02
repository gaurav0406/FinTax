import re

with open("backend/financial_news_scraper.py", "r", encoding="utf-8") as f:
    content = f.read()

old_func = """def call_gemini_batch_api(items: list) -> dict:
    if not GEMINI_API_KEY or "YOUR_" in GEMINI_API_KEY:
        logging.warning("No valid GEMINI_API_KEY found. Utilizing local NLP fallback.")
        return {}
    simplified = [{"id": item["id"], "title": item["title"], "content": item["text"]} for item in items]
    
    prompt = f\"\"\"You are an expert Indian Financial News & Tax Journalist.Analyze these news items and produce structured, actionable JSON summaries tailored for Indian taxpayers and retail investors.Output MUST be strictly valid JSON without markdown code blocks.DO NOT use introductory labels like "Key Update:", "Why it matters:", "Source Report:", "Investor Takeaway:", "Monetary Outlook:", "Market Context:", "Verify Details:", or "Portfolio Review:" in your bullet points. The output must be crisp, concise, and direct.Input Items:{json.dumps(simplified, indent=2)}Respond ONLY with a JSON Array of objects matching this exact format for each item:[  {{    "id": <number matching input id>,    "title": "Catchy headline for Indian taxpayers/investors (Max 10 words)",    "summary": "Provide a detailed 4 to 5 line summary of the article covering all key points.",    "who_impacted": "Salaried Employees, Individual Taxpayers & Investors",    "reason": "Provide EXACTLY 3 to 4 crisp bullet points (using '• '). Each bullet point MUST NOT exceed 1 single line (max 12 words). Explain why this news matters and core drivers. Do NOT use introductory labels.",    "financial_impact": "Provide EXACTLY 3 to 4 crisp bullet points (using '• '). Each bullet point MUST NOT exceed 1 single line and MUST start with a clear numerical metric, percentage, KPI, or monetary advantage (e.g. '• +2.5% Rate Cut: ...', '• ₹4,800 Savings: ...', '• 15% Cashback: ...'). Do NOT use introductory labels.",    "action": "Provide EXACTLY 3 to 4 crisp bullet points (using '• '). Each bullet point MUST NOT exceed 1 single line (max 12 words) detailing actionable steps or recommendations. Do NOT use introductory labels.",    "category": "Must be EXACTLY ONE of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'Stock Market India', 'Startup Ecosystem']"  }}]\"\"\"
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={GEMINI_API_KEY}"
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {"response_mime_type": "application/json"}
    }
    try:
        req = urllib.request.Request(
            url,
            data=json.dumps(payload).encode('utf-8'),
            headers={'Content-Type': 'application/json'},
            method='POST'
        )
        with urllib.request.urlopen(req, timeout=12) as resp:
            data = json.loads(resp.read().decode('utf-8'))
            text_resp = data["candidates"][0]["content"]["parts"][0]["text"].strip()
            
            if text_resp.startswith("```json"): text_resp = text_resp[7:]
            if text_resp.startswith("```"): text_resp = text_resp[3:]
            if text_resp.endswith("```"): text_resp = text_resp[:-3]
            
            parsed_list = json.loads(text_resp.strip())
            return {int(obj["id"]): obj for obj in parsed_list if "id" in obj}
    except Exception as e:
        logging.error(f"Gemini API call failed: {e}")
        return {}"""

new_func = """def call_gemini_batch_api(items: list) -> dict:
    if not GEMINI_API_KEY or "YOUR_" in GEMINI_API_KEY:
        logging.warning("No valid GEMINI_API_KEY found. Utilizing local NLP fallback.")
        return {}
    simplified = [{"id": item["id"], "title": item["title"], "content": item["text"]} for item in items]
    
    prompt = f\"\"\"You are an expert Indian Financial News & Tax Journalist.Analyze these news items and produce structured, actionable JSON summaries tailored for Indian taxpayers and retail investors.Output MUST be strictly valid JSON without markdown code blocks.DO NOT use introductory labels like "Key Update:", "Why it matters:", "Source Report:", "Investor Takeaway:", "Monetary Outlook:", "Market Context:", "Verify Details:", or "Portfolio Review:" in your bullet points. The output must be crisp, concise, and direct.Input Items:{json.dumps(simplified, indent=2)}Respond ONLY with a JSON Array of objects matching this exact format for each item:[  {{    "id": <number matching input id>,    "title": "Catchy headline for Indian taxpayers/investors (Max 10 words)",    "summary": "Provide a detailed 4 to 5 line summary of the article covering all key points.",    "who_impacted": "Salaried Employees, Individual Taxpayers & Investors",    "reason": "Provide EXACTLY 3 to 4 crisp bullet points (using '• '). Each bullet point MUST NOT exceed 1 single line (max 12 words). Explain why this news matters and core drivers. Do NOT use introductory labels.",    "financial_impact": "Provide EXACTLY 3 to 4 crisp bullet points (using '• '). Each bullet point MUST NOT exceed 1 single line and MUST start with a clear numerical metric, percentage, KPI, or monetary advantage (e.g. '• +2.5% Rate Cut: ...', '• ₹4,800 Savings: ...', '• 15% Cashback: ...'). Do NOT use introductory labels.",    "action": "Provide EXACTLY 3 to 4 crisp bullet points (using '• '). Each bullet point MUST NOT exceed 1 single line (max 12 words) detailing actionable steps or recommendations. Do NOT use introductory labels.",    "category": "Must be EXACTLY ONE of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'Stock Market India', 'Startup Ecosystem']"  }}]\"\"\"
    
    urls = [
        f"https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key={GEMINI_API_KEY}",
        f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={GEMINI_API_KEY}"
    ]
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {"response_mime_type": "application/json"}
    }
    
    for url in urls:
        for attempt in range(2):
            try:
                req = urllib.request.Request(
                    url,
                    data=json.dumps(payload).encode('utf-8'),
                    headers={'Content-Type': 'application/json'},
                    method='POST'
                )
                with urllib.request.urlopen(req, timeout=20) as resp:
                    data = json.loads(resp.read().decode('utf-8'))
                    text_resp = data["candidates"][0]["content"]["parts"][0]["text"].strip()
                    
                    if text_resp.startswith("```json"): text_resp = text_resp[7:]
                    if text_resp.startswith("```"): text_resp = text_resp[3:]
                    if text_resp.endswith("```"): text_resp = text_resp[:-3]
                    
                    parsed_list = json.loads(text_resp.strip())
                    return {int(obj["id"]): obj for obj in parsed_list if "id" in obj}
            except urllib.error.HTTPError as e:
                if e.code == 429:
                    logging.warning(f"Rate limited (429). Retrying after backoff (attempt {attempt+1})...")
                    time.sleep(2 * (attempt + 1))
                else:
                    logging.error(f"Gemini API HTTP Error ({e.code}): {e}")
                    break
            except Exception as e:
                logging.error(f"Gemini API call failed: {e}")
                time.sleep(1)
                break
    return {}"""

if old_func in content:
    content = content.replace(old_func, new_func)
else:
    print("Warning: old_func exact match not found, checking alternative replacement")

# Update main function call
old_main_call = 'llm_map = call_gemini_batch_api(items_to_process) if items_to_process else {}'
new_main_call = '''llm_map = {}
    if items_to_process:
        batch_size = 12
        for i in range(0, len(items_to_process), batch_size):
            batch = items_to_process[i:i+batch_size]
            logging.info(f"Processing batch {i//batch_size + 1} ({len(batch)} articles)...")
            batch_res = call_gemini_batch_api(batch)
            if batch_res:
                llm_map.update(batch_res)
            else:
                logging.warning(f"Batch {i//batch_size + 1} failed or timed out. Falling back to local NLP summaries for this batch.")
            time.sleep(1.0)'''

if old_main_call in content:
    content = content.replace(old_main_call, new_main_call)

with open("backend/financial_news_scraper.py", "w", encoding="utf-8") as f:
    f.write(content)
print("Successfully patched backend/financial_news_scraper.py")
