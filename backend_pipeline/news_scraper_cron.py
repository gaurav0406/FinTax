#!/usr/bin/env python3
"""
FinTax Audio News - High-Performance Zero-Dependency Financial News Scraper & NLP Pipeline

Features:
1. Hybrid scraping: Combines RSS feeds with Free Financial APIs (e.g., Finnhub)
2. Gemini 2.0 Flash batch structured summarization via REST API.
3. Google News Architecture: Fixed top-level Categories + Dynamic Topic Clusters.
4. Generates local JSON artifacts: processed_scraped_data.json & raw_scraped_data.json.
5. Direct Supabase REST API insert/upsert integration with exact table schema.
"""
import os
import sys
import time
import json
import logging
import re
import urllib.request
import urllib.parse
from html import unescape

logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s')

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")
SUPABASE_URL = os.getenv("SUPABASE_URL", "")
SUPABASE_KEY = os.getenv("SUPABASE_SERVICE_ROLE_KEY", os.getenv("SUPABASE_KEY", ""))
FINNHUB_API_KEY = os.getenv("FINNHUB_API_KEY", "")

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
RAW_DATA_FILE = os.path.join(BASE_DIR, "raw_scraped_data.json")
PROCESSED_DATA_FILE = os.path.join(BASE_DIR, "processed_scraped_data.json")

FEEDS = [
    {
        "category": "Financial News",
        "url": "https://www.livemint.com/rss/money",
        "sourceName": "LiveMint Personal Finance"
    },
    {
        "category": "Financial News",
        "url": "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
        "sourceName": "Economic Times Top Stories"
    },
    {
        "category": "Credit Cards",
        "url": "https://www.forbes.com/advisor/credit-cards/feed/",
        "sourceName": "Forbes Advisor"
    },
    {
        "category": "Mutual Funds",
        "url": "https://www.livemint.com/rss/mutual-funds",
        "sourceName": "LiveMint Mutual Funds"
    },
    {
        "category": "Sports",
        "url": "https://www.espn.com/espn/rss/news",
        "sourceName": "ESPN Top News"
    },
    {
        "category": "Cars & EVs",
        "url": "https://electrek.co/feed/",
        "sourceName": "Electrek"
    },
    {
        "category": "Education",
        "url": "https://www.edweek.org/feed",
        "sourceName": "Education Week"
    },
    {
        "category": "Crypto",
        "url": "https://cointelegraph.com/rss",
        "sourceName": "CoinTelegraph"
    },
    {
        "category": "Technology",
        "url": "https://techcrunch.com/feed/",
        "sourceName": "TechCrunch"
    }
]

def clean_html_text(text: str) -> str:
    text = unescape(text)
    text = re.sub(r'<[^>]+>', '', text)
    text = re.sub(r'\s+', ' ', text).strip()
    return text

def fetch_api_news_finnhub(max_items: int = 10) -> list:
    if not FINNHUB_API_KEY or "YOUR_" in FINNHUB_API_KEY:
        logging.info("Finnhub API key not provided, skipping API fetch.")
        return []
    
    items = []
    try:
        url = f"https://finnhub.io/api/v1/news?category=general&token={FINNHUB_API_KEY}"
        req = urllib.request.Request(url)
        with urllib.request.urlopen(req, timeout=10) as response:
            data = json.loads(response.read().decode('utf-8'))
            
            for article in data[:max_items]:
                items.append({
                    "title": article.get("headline", "")[:250],
                    "url": article.get("url", ""),
                    "text": article.get("summary", "")[:1000],
                    "category": "Financial News",
                    "sourceName": article.get("source", "Finnhub API")
                })
    except Exception as e:
        logging.warning(f"Error fetching from Finnhub API: {e}")
    
    return items

def fetch_rss_feed_fast(feed_info: dict, max_items: int = 10) -> list:
    items = []
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
    try:
        req = urllib.request.Request(feed_info["url"], headers=headers)
        with urllib.request.urlopen(req, timeout=8) as response:
            xml_data = response.read().decode('utf-8', errors='ignore')
        
        xml_clean = re.sub(r'[\x00-\x08\x0B\x0C\x0E-\x1F]', '', xml_data)
        item_blocks = re.findall(r'<item>(.*?)</item>', xml_clean, re.DOTALL | re.IGNORECASE)
        
        for block in item_blocks[:max_items]:
            cb = block.replace('<![CDATA[', '').replace(']]>', '')
            title_m = re.search(r'<title>(.*?)</title>', cb, re.DOTALL | re.IGNORECASE)
            link_m = re.search(r'<link>(.*?)</link>', cb, re.DOTALL | re.IGNORECASE)
            desc_m = re.search(r'<(?:description|summary)>(.*?)</(?:description|summary)>', cb, re.DOTALL | re.IGNORECASE)
            
            title = clean_html_text(title_m.group(1)) if title_m else ""
            link = clean_html_text(link_m.group(1)) if link_m else ""
            desc = clean_html_text(desc_m.group(1)) if desc_m else title
            
            if title and link:
                clean_link = link.split("?")[0]
                items.append({
                    "title": title[:250],
                    "url": clean_link,
                    "text": desc[:1000] if len(desc) > 10 else title,
                    "category": feed_info["category"],
                    "sourceName": feed_info["sourceName"]
                })
    except Exception as e:
        logging.warning(f"Error reading feed {feed_info['url']}: {e}")
    return items

def call_gemini_batch_api(items: list) -> dict:
    if not GEMINI_API_KEY or "YOUR_" in GEMINI_API_KEY:
        logging.warning("No valid GEMINI_API_KEY found. Utilizing local NLP fallback.")
        return {}

    simplified = [{"id": item["id"], "title": item["title"], "content": item["text"]} for item in items]
    
    prompt = f"""You are an expert Journalist and Analyst.
Analyze these news items and produce structured, actionable JSON summaries.
If the news is about Mutual Funds, emphasize fund performance (best/lowest).
If the news is about Credit Cards, highlight updates to policies and rewards.

Output MUST be strictly valid JSON without markdown code blocks.

Input Items:
{json.dumps(simplified, indent=2)}

Respond ONLY with a JSON Array of objects matching this exact format for each item:
[
  {{
    "id": <number matching input id>,
    "title": "Catchy headline (Max 10 words)",
    "summary": "Detailed 5-6 line summary of the article, covering all key points.",
    "who_impacted": "Who does this impact? (e.g. Salaried Employees, Tech Enthusiasts, Sports Fans)",
    "reason": "Why it matters: Provide 3-4 bullet points explaining the core reasons or causes.",
    "financial_impact": "Financial impacts/benefits: Provide 3-4 bullet points detailing monetary effects or benefits.",
    "action": "Actionable steps: Provide 3-4 bullet points of suggestions on what a user/investor/reader should do.",
    "category": "Must be EXACTLY ONE of ['Financial News', 'Credit Cards', 'Mutual Funds', 'Sports', 'Cars & EVs', 'Education', 'Crypto', 'Technology']",
    "topic_cluster": "Dynamic 2-3 word topic tag (e.g. 'Tech Earnings', 'RBI Policy', 'EV Market')"
  }}
]
"""

    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={GEMINI_API_KEY}"
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {"response_mime_type": "application/json"}
    }

    max_retries = 4
    for attempt in range(max_retries):
        try:
            req = urllib.request.Request(
                url,
                data=json.dumps(payload).encode('utf-8'),
                headers={'Content-Type': 'application/json'},
                method='POST'
            )
            with urllib.request.urlopen(req, timeout=25) as resp:
                data = json.loads(resp.read().decode('utf-8'))
                text_resp = data["candidates"][0]["content"]["parts"][0]["text"].strip()
                
                if text_resp.startswith("```json"): text_resp = text_resp[7:]
                if text_resp.startswith("```"): text_resp = text_resp[3:]
                if text_resp.endswith("```"): text_resp = text_resp[:-3]
                
                parsed_list = json.loads(text_resp.strip())
                return {int(obj["id"]): obj for obj in parsed_list if "id" in obj}
        except urllib.error.HTTPError as e:
            if e.code == 429:
                delay = 2 ** attempt * 5  # 5s, 10s, 20s, 40s
                logging.warning(f"Rate limited (429). Retrying in {delay} seconds...")
                time.sleep(delay)
            else:
                logging.error(f"Gemini API HTTP Error: {e}")
                break
        except Exception as e:
            logging.error(f"Gemini API call failed: {e}")
            break
            
    return {}

def generate_fallback_llm_summary(item: dict) -> dict:
    title = item["title"]
    category = item["category"]
    
    return {
        "id": item["id"],
        "title": title[:250],
        "summary": f"Key update regarding {title}. Here is a 5-6 line placeholder summary because the NLP service was unavailable.",
        "who_impacted": "General Audience",
        "reason": "• Point 1 about why it matters\n• Point 2 about why it matters\n• Point 3 about why it matters",
        "financial_impact": "• Point 1 on financial impact\n• Point 2 on financial impact\n• Point 3 on financial impact",
        "action": "• Action step 1\n• Action step 2\n• Action step 3",
        "category": category,
        "topic_cluster": "Latest Updates"
    }

def push_to_supabase_rest(records: list):
    if not SUPABASE_URL or not SUPABASE_KEY or "YOUR_" in SUPABASE_URL:
        logging.info("Supabase push skipped (credentials not set).")
        return

    url = f"{SUPABASE_URL.rstrip('/')}/rest/v1/financial_news"
    headers = {
        'apikey': SUPABASE_KEY,
        'Authorization': f'Bearer {SUPABASE_KEY}',
        'Content-Type': 'application/json',
        'Prefer': 'resolution=merge-duplicates'
    }

    now_ms = int(time.time() * 1000)
    payload = []

    for r in records:
        llm = r["llm_summary"]
        payload.append({
            "title": r["title"][:250],
            "summaryWhatHappened": llm.get("summary", "")[:1000],
            "summaryWhoImpacted": llm.get("who_impacted", "")[:500],
            "summaryActionableTakeaway": llm.get("action", "")[:1000],
            "summaryText": llm.get("reason", "")[:1500],
            "category": r["category"][:50],
            "financialActionUrl": "https://example.com",
            "sourceUrl": r["url"],
            "sourceName": r["sourceName"][:90],
            "imageUrl": "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",
            "financialImpactBullets": llm.get("financial_impact", "")[:1500],
            "publishedAt": now_ms
        })

    try:
        req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method='POST')
        with urllib.request.urlopen(req, timeout=10) as resp:
            logging.info(f"Supabase REST push SUCCESS! Status: {resp.status}")
    except urllib.error.HTTPError as e:
        error_body = e.read().decode('utf-8')
        logging.error(f"Supabase REST upload failed: HTTP Error {e.code}: {e.reason} - {error_body}")
    except Exception as e:
        logging.error(f"Supabase REST upload failed: {e}")

def main():
    start_time = time.time()
    logging.info("Starting hybrid API + RSS news scraper & artifact generator...")

    raw_items = []
    seen_urls = set()
    item_id = 1

    # 1. Fetch from Free API (if key present)
    api_items = fetch_api_news_finnhub(max_items=5)
    for it in api_items:
        if it["url"] not in seen_urls:
            it["id"] = item_id
            raw_items.append(it)
            seen_urls.add(it["url"])
            item_id += 1

    # 2. Fetch from RSS Feeds
    for feed in FEEDS:
        items = fetch_rss_feed_fast(feed, max_items=10)
        for it in items:
            if it["url"] not in seen_urls:
                it["id"] = item_id
                raw_items.append(it)
                seen_urls.add(it["url"])
                item_id += 1

    logging.info(f"Collected {len(raw_items)} raw articles in {time.time() - start_time:.2f} seconds.")

    # Call Gemini in batches of 15 to avoid large payload errors
    llm_map = {}
    batch_size = 15
    for i in range(0, len(raw_items), batch_size):
        batch = raw_items[i:i+batch_size]
        batch_map = call_gemini_batch_api(batch)
        llm_map.update(batch_map)
        time.sleep(5) # Strict rate limit to avoid 429s

    processed_list = []
    for item in raw_items:
        summary_obj = llm_map.get(item["id"], generate_fallback_llm_summary(item))
        cat = summary_obj.get("category", item["category"])
        
        processed_item = {
            "id": item["id"],
            "title": summary_obj.get("title", item["title"])[:250],
            "url": item["url"],
            "text": item["text"],
            "category": cat,
            "sourceName": item["sourceName"],
            "imageUrl": "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",
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
        processed_list.append(processed_item)

    # Save artifacts
    dirs_to_save = [
        BASE_DIR,
        os.path.join(BASE_DIR, "..", "backend"),
        os.path.join(BASE_DIR, "..")
    ]
    
    for target_dir in dirs_to_save:
        if os.path.exists(target_dir):
            try:
                raw_path = os.path.join(target_dir, "raw_scraped_data.json")
                proc_path = os.path.join(target_dir, "processed_scraped_data.json")
                with open(raw_path, "w", encoding="utf-8") as f:
                    json.dump(raw_items, f, indent=2, ensure_ascii=False)
                with open(proc_path, "w", encoding="utf-8") as f:
                    json.dump(processed_list, f, indent=2, ensure_ascii=False)
                logging.info(f"Saved artifacts to {proc_path}")
            except Exception as e:
                logging.error(f"Error writing artifacts to {target_dir}: {e}")

    # Push to Supabase REST API
    push_to_supabase_rest(processed_list)

    elapsed = time.time() - start_time
    logging.info(f"Pipeline finished successfully in {elapsed:.2f} seconds! Total articles: {len(processed_list)}")

if __name__ == "__main__":
    main()
