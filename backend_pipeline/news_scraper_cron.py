import os
import json
import time
import logging
import hashlib
from difflib import SequenceMatcher
import feedparser
from bs4 import BeautifulSoup
from google import genai
from google.genai import types
from supabase import create_client, Client
from googleapiclient.discovery import build

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logging.getLogger("google").setLevel(logging.WARNING)
logging.getLogger("google.genai").setLevel(logging.WARNING)
logging.getLogger("httpx").setLevel(logging.WARNING)
logging.getLogger("urllib3").setLevel(logging.WARNING)

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY")
SUPABASE_URL = os.getenv("SUPABASE_URL", "YOUR_SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY", "YOUR_SUPABASE_SERVICE_ROLE_KEY")
YOUTUBE_API_KEY = os.getenv("YOUTUBE_API_KEY", "YOUR_YOUTUBE_API_KEY")

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
RAW_DATA_FILE = os.path.join(BASE_DIR, "raw_scraped_data.json")
PROCESSED_DATA_FILE = os.path.join(BASE_DIR, "processed_scraped_data.json")

gemini_client = None
try:
    if GEMINI_API_KEY and "YOUR_" not in GEMINI_API_KEY:
        gemini_client = genai.Client(api_key=GEMINI_API_KEY)
        logging.info("Gemini client initialized successfully.")
    else:
        logging.warning("GEMINI_API_KEY not set or invalid placeholder.")
except Exception as e:
    logging.error(f"Error configuring Gemini API: {e}")

supabase: Client = None
try:
    if (SUPABASE_URL and SUPABASE_URL.startswith("http") and "YOUR_" not in SUPABASE_URL and
        SUPABASE_KEY and len(SUPABASE_KEY) > 20 and "YOUR_" not in SUPABASE_KEY):
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)
        logging.info("Supabase client initialized successfully.")
except Exception as e:
    logging.warning(f"Failed to initialize Supabase client: {e}")
    supabase = None

youtube = None
try:
    if YOUTUBE_API_KEY and "YOUR_" not in YOUTUBE_API_KEY:
        youtube = build('youtube', 'v3', developerKey=YOUTUBE_API_KEY)
        logging.info("YouTube client initialized successfully.")
except Exception as e:
    logging.warning(f"Failed to initialize YouTube client: {e}")
    youtube = None

def get_md5_hash(text: str) -> str:
    return hashlib.md5(text.encode('utf-8')).hexdigest()

def similar(a, b):
    return SequenceMatcher(None, a, b).ratio()

def fetch_financial_news():
    logging.info("Scraping financial news...")
    
    feeds = [
        {"category": "Stock Market India", "url": "https://economictimes.indiatimes.com/markets/rssfeeds/2146842.cms"},
        {"category": "ITR & Income Tax", "url": "https://economictimes.indiatimes.com/wealth/tax/rssfeeds/83755913.cms"},
        {"category": "Mutual Funds & Wealth", "url": "https://economictimes.indiatimes.com/mf/rssfeeds/83756208.cms"},
        {"category": "Personal Finance & Banking", "url": "https://www.livemint.com/rss/money"},
        {"category": "Government Schemes & RBI", "url": "https://www.financialexpress.com/about/gst/feed/"},
        {"category": "Crypto & Economy", "url": "https://economictimes.indiatimes.com/markets/cryptocurrency/rssfeeds/82519373.cms"}
    ]
    
    articles = []
    seen_urls_md5 = set()
    seen_titles = []
    item_id = 1

    for feed in feeds:
        try:
            parsed = feedparser.parse(feed["url"])
            count = 0
            for entry in parsed.entries:
                if count >= 10:
                    break
                
                title = entry.get("title", "").strip()
                url = entry.get("link", "").strip()
                if not title or not url:
                    continue

                url_clean = url.split("?")[0]
                url_hash = get_md5_hash(url_clean)
                if url_hash in seen_urls_md5:
                    continue

                norm_title = "".join(c.lower() for c in title if c.isalnum())
                is_duplicate = False
                for st in seen_titles:
                    if similar(norm_title, st) > 0.8:
                        is_duplicate = True
                        break
                if is_duplicate:
                    continue

                summary_html = entry.get("summary", "")
                soup = BeautifulSoup(summary_html, 'html.parser')
                clean_text = soup.get_text(strip=True)
                
                if not clean_text:
                    clean_text = title
                
                articles.append({
                    "id": item_id,
                    "is_video": False,
                    "title": title,
                    "url": url_clean,
                    "text": clean_text,
                    "category": feed["category"],
                    "sourceName": "Indian Financial News Feed",
                    "imageUrl": None
                })
                seen_urls_md5.add(url_hash)
                seen_titles.append(norm_title)
                item_id += 1
                count += 1
        except Exception as e:
            logging.error(f"Error fetching RSS feed {feed['url']}: {e}")
            
    return articles, item_id

def fetch_youtube_shorts(start_id: int):
    logging.info("Fetching YouTube Finance Videos...")
    if not youtube:
        return []
        
    categories = [
        {"category": "Stock Market India", "query": "Indian stock market Sensex Nifty share market news 2026 shorts"},
        {"category": "Personal Finance & Banking", "query": "Indian personal finance banking tips 2026 shorts"},
    ]
    
    videos = []
    item_id = start_id
    for cat in categories:
        try:
            request = youtube.search().list(
                part="snippet",
                q=cat["query"],
                maxResults=5,
                type="video"
            )
            response = request.execute()
            
            for item in response.get("items", []):
                snippet = item["snippet"]
                videos.append({
                    "id": item_id,
                    "is_video": True,
                    "title": snippet["title"],
                    "url": f"https://www.youtube.com/watch?v={item['id']['videoId']}",
                    "text": snippet["description"],
                    "sourceName": snippet["channelTitle"],
                    "category": cat["category"],
                    "imageUrl": snippet["thumbnails"]["high"]["url"]
                })
                item_id += 1
        except Exception as e:
            logging.error(f"Error fetching YouTube videos: {e}")
            
    return videos

def summarize_batch_with_gemini(items: list) -> dict:
    if not items or not gemini_client:
        return {}

    simplified_items = [{"id": i["id"], "title": i["title"], "content": i["text"][:1500]} for i in items]

    prompt = f"""You are an expert financial news editor and summarizer. Process the following list of news/video items and summarize EACH item into JSON format.

CRITICAL RULES:
1. ALL TEXT IN YOUR RESPONSE MUST BE IN CLEAN, FLUENT, COMPLETE ENGLISH. Translate any non-English text automatically.
2. Provide information in concise BULLET POINTS.
3. Categorize each item strictly into EXACTLY ONE of these standard categories:
   - "ITR & Income Tax"
   - "Stock Market India"
   - "Mutual Funds & Wealth"
   - "Personal Finance & Banking"
   - "Government Schemes & RBI"
   - "Crypto & Economy"

Input Items:
{json.dumps(simplified_items, indent=2)}

Respond ONLY with a JSON Array containing an object for each item with these exact keys:
[
  {{
    "id": <number matching input id>,
    "summary": "Clear 4-line summary of the news in English in bullet points.",
    "who_impacted": "1 to 2 lines specifying who is impacted by this news.",
    "reason": "3 to 4 lines explaining why this decision or market event occurred, in bullet points.",
    "financial_impact": "Financial impact or monetary numbers in 3 to 4 bullet points.",
    "action": "Actionable financial steps in 3 to 4 lines in bullet points.",
    "category": "One of the strict categories",
    "key_metrics": ["Metric 1", "Metric 2"],
    "jargon_terms": {{"Jargon1": "Explanation in plain English", "Jargon2": "Explanation"}}
  }}
]
"""
    models_to_try = ['gemini-3.5-flash', 'gemini-3.1-pro-preview']
    for model_name in models_to_try:
        for attempt in range(3):
            try:
                response = gemini_client.models.generate_content(
                    model=model_name,
                    contents=prompt,
                    config=types.GenerateContentConfig(
                        response_mime_type="application/json",
                        automatic_function_calling=types.AutomaticFunctionCallingConfig(disable=True)
                    )
                )
                response_text = response.text.strip()
                if response_text.startswith("```json"): response_text = response_text[7:]
                if response_text.startswith("```"): response_text = response_text[3:]
                if response_text.endswith("```"): response_text = response_text[:-3]
                
                parsed_list = json.loads(response_text)
                if isinstance(parsed_list, dict):
                    parsed_list = parsed_list.get("items", parsed_list.get("data", [parsed_list]))
                
                summaries_by_id = {}
                for summary in parsed_list:
                    if isinstance(summary, dict) and "id" in summary:
                        summaries_by_id[int(summary["id"])] = summary
                return summaries_by_id
            except Exception as e:
                time.sleep(10 * (attempt + 1))
    return {}

def push_batch_to_supabase(payloads: list):
    if not payloads or not supabase: return
    try:
        supabase.table("financial_news").insert(payloads).execute()
    except Exception as e:
        for payload in payloads:
            try: supabase.table("financial_news").insert(payload).execute()
            except: pass

def main():
    articles, next_id = fetch_financial_news()
    videos = fetch_youtube_shorts(start_id=next_id)
    all_raw_items = articles + videos

    CHUNK_SIZE = 15
    summaries_map = {}
    for i in range(0, len(all_raw_items), CHUNK_SIZE):
        chunk = all_raw_items[i : i + CHUNK_SIZE]
        summaries_map.update(summarize_batch_with_gemini(chunk))
        time.sleep(2)

    now_ms = int(time.time() * 1000)
    
    # 5 Articles Per Category Rule
    category_counts = {}
    payloads = []

    for item in all_raw_items:
        item_id = item["id"]
        llm_data = summaries_map.get(item_id, {})
        if not llm_data: continue

        category = llm_data.get("category", "Stock Market India")
        if category_counts.get(category, 0) >= 5:
            continue
        category_counts[category] = category_counts.get(category, 0) + 1

        impact_data = {
            "impact": llm_data.get("financial_impact", ""),
            "metrics": llm_data.get("key_metrics", []),
            "jargon": llm_data.get("jargon_terms", {})
        }

        payload = {
            "title": item["title"],
            "sourceUrl": item["url"],
            "summaryWhatHappened": llm_data.get("summary", ""),
            "summaryWhoImpacted": llm_data.get("who_impacted", ""),
            "summaryText": llm_data.get("reason", ""),
            "summaryActionableTakeaway": llm_data.get("action", ""),
            "financialImpactBullets": json.dumps(impact_data),
            "category": category,
            "sourceName": item.get("sourceName", ""),
            "imageUrl": item.get("imageUrl", None),
            "publishedAt": now_ms
        }
        payloads.append(payload)

    push_batch_to_supabase(payloads)

if __name__ == "__main__":
    main()
