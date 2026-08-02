#!/usr/bin/env python3
"""
FinTax Audio News - High-Performance Zero-Dependency Financial News Scraper & NLP Pipeline

Scheduled Execution: Runs automatically every 24 hours via GitHub Actions ('0 0 * * *').

Features:
1. Hybrid scraping: Combines RSS feeds with Free Financial APIs (e.g., Finnhub)
2. Gemini 3.5 / 2.0 Flash batch structured summarization via REST API.
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
        "url": "https://www.financialexpress.com/money/feed/",
        "sourceName": "Financial Express Money"
    },
    {
        "category": "Credit Cards",
        "url": "https://economictimes.indiatimes.com/wealth/spend/rssfeeds/83815340.cms",
        "sourceName": "Economic Times Spend & Cards"
    },
    {
        "category": "Credit Cards",
        "url": "https://paisabazaar.com/blog/category/credit-card/feed/",
        "sourceName": "Paisabazaar Credit Cards"
    },
    {
        "category": "Credit Cards",
        "url": "https://www.livemint.com/rss/banking",
        "sourceName": "LiveMint Banking & Cards"
    },

    # Financial News Sources (Non-Moneycontrol)
    {
        "category": "Financial News",
        "url": "https://www.businesstoday.in/rss/topstories",
        "sourceName": "Business Today Top Stories"
    },
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
        "category": "Financial News",
        "url": "https://economictimes.indiatimes.com/wealth/rssfeedstopstories.cms",
        "sourceName": "Economic Times Wealth"
    },
    {
        "category": "Financial News",
        "url": "https://www.financialexpress.com/feed/",
        "sourceName": "Financial Express Main"
    },

    # Markets & Mutual Funds
    {
        "category": "Markets & Mutual Funds",
        "url": "https://www.livemint.com/rss/mutual-funds",
        "sourceName": "LiveMint Mutual Funds"
    },
    {
        "category": "Markets & Mutual Funds",
        "url": "https://economictimes.indiatimes.com/markets/rssfeeds/1977021501.cms",
        "sourceName": "Economic Times Markets"
    },
    {
        "category": "Markets & Mutual Funds",
        "url": "https://www.livemint.com/rss/markets",
        "sourceName": "LiveMint Markets"
    },

    # Technology & AI
    {
        "category": "Technology",
        "url": "https://feeds.feedburner.com/gadgets360-latest",
        "sourceName": "Gadgets 360 India"
    },
    {
        "category": "Technology",
        "url": "https://techcrunch.com/feed/",
        "sourceName": "TechCrunch"
    },
    {
        "category": "Technology",
        "url": "https://economictimes.indiatimes.com/tech/rssfeeds/13357270.cms",
        "sourceName": "Economic Times Tech"
    },

    # Cars & EVs
    {
        "category": "Cars & EVs",
        "url": "https://www.rushlane.com/feed",
        "sourceName": "RushLane Auto India"
    },
    {
        "category": "Cars & EVs",
        "url": "https://drivespark.com/rss/feeds.xml",
        "sourceName": "DriveSpark Auto"
    },

    # Crypto
    {
        "category": "Crypto",
        "url": "https://cointelegraph.com/rss",
        "sourceName": "CoinTelegraph"
    },
    {
        "category": "Crypto",
        "url": "https://www.coindesk.com/arc/outboundfeeds/rss/",
        "sourceName": "CoinDesk"
    }
]

def detect_category(title: str, text: str, feed_category: str) -> str:
    content = (title + " " + text).lower()
    
    # Filter out foreign / US credit card news without India context
    non_india_keywords = [
        "chase sapphire", "delta sky", "united miles", "southwest rapid", 
        "capital one us", "american express us", "tsa precheck", "global entry us"
    ]
    if any(k in content for k in non_india_keywords) and not any(ik in content for ik in ["india", "hdfc", "sbi", "rbi", "rupay", "icici", "axis"]):
        return "Financial News"

    # 1. Credit Cards (Strictly India & General Cards)
    if any(k in content for k in [
        "credit card", "credit cards", "card reward", "lounge access", "cashback card", 
        "card annual fee", "cardholders", "debit card", "sbi card", "hdfc card", 
        "axis card", "icici card", "rupay card", "visa card", "mastercard"
    ]):
        return "Credit Cards"
        
    # 2. ITR & Tax
    if any(k in content for k in [
        "income tax", "itr", "tax return", "tax filing", "tax slab", "section 80c", 
        "tax refund", "tax deduction", "tds", "capital gains tax", "tax exemption", 
        "direct tax", "advance tax", "form 16", "taxpayer", "itat"
    ]):
        return "ITR & Tax"
        
    # 3. Mutual Funds & Investing
    if any(k in content for k in [
        "mutual fund", "mutual funds", "sip", "systematic investment", "nav", 
        "elss", "equity fund", "debt fund", "amfi", "index fund", "etf", 
        "asset management", "small cap", "mid cap", "large cap", "nfo", "pms"
    ]):
        return "Mutual Funds"
        
    # 4. Loans & FDs
    if any(k in content for k in [
        "home loan", "personal loan", "car loan", "education loan", "emi", 
        "interest rate", "fixed deposit", "fd rate", "recurring deposit", 
        "bank fd", "pnb fd", "sbi fd", "tenure", "collateral", "mortgage"
    ]):
        return "Loans & FDs"
        
    # 5. Stock Market India / Earnings
    if any(k in content for k in [
        "sensex", "nifty", "stock market", "shares", "q1 results", "q2 results", 
        "q3 results", "q4 results", "net profit", "quarterly profit", "bse", "nse", 
        "ipo", "drhp", "dividend", "market wrap", "gainers", "losers", "bull market", "bear market", "d-street", "dalal street"
    ]):
        return "Markets & Mutual Funds"
        
    # 6. RBI & Policy / Banking
    if any(k in content for k in [
        "rbi", "reserve bank", "monetary policy", "repo rate", "mpc", "central bank", 
        "bank regulation", "inflation rate", "cpi inflation", "forex reserve"
    ]):
        return "RBI & Policy"
        
    # 7. Crypto
    if any(k in content for k in [
        "bitcoin", "crypto", "cryptocurrency", "ethereum", "blockchain", "btc", 
        "eth", "coinbase", "binance", "altcoin", "nft"
    ]):
        return "Crypto"
        
    # 8. Cars & EVs
    if any(k in content for k in [
        "electric vehicle", "ev ", "evs", "tesla", "e-bike", "robotaxi", "battery cell", "automobile", "charging station", "car launch", "suv", "hybrid car"
    ]):
        return "Cars & EVs"

    # 9. Gaming
    if any(k in content for k in [
        "xbox", "playstation", "nintendo", "pc gaming", "steam", "esports", "twitch", "gameplay", "ps5", "gta", "fortnite"
    ]):
        return "Gaming"

    # 10. Technology & AI
    if any(k in content for k in [
        "artificial intelligence", "ai", "chatgpt", "openai", "google gemini", "apple", "iphone", "android", "smartphone", "semiconductor", "cybersecurity", "software"
    ]):
        return "Technology"

    # 11. Sports
    if any(k in content for k in [
        "cricket", "ipl", "bcci", "football", "premier league", "champions league", "tennis", "olympics", "world cup"
    ]):
        return "Sports"
        
    # Default to feed category if specific, otherwise "Financial News"
    if feed_category in ["Credit Cards", "Mutual Funds", "Crypto", "Cars & EVs", "Technology", "Sports", "Education", "Entertainment", "Gaming", "ITR & Tax", "Loans & FDs", "Markets & Mutual Funds", "RBI & Policy"]:
        return feed_category
        
    return "Financial News"


def map_category(engine_cat: str) -> str:
    mapping = {
        "FREELANCER_REMOTE_FINANCE": "Card Hacks & Perks",
        "ESOP_STARTUP_EQUITY": "Startup & Capital",
        "SME_D2C_FINTECH": "Financial Markets",
        "FIRE_HIGH_INCOME_TECH": "Wealth 101",
        "WEB3_ALTERNATIVE_ASSETS": "Tech & AI"
    }
    return mapping.get(engine_cat, "Wealth 101")

def clean_html_text(text: str) -> str:
    text = unescape(text)
    text = re.sub(r'<[^>]+>', '', text)
    text = re.sub(r'(?i)^\s*(published\s+by|home\b|copyright|all\s+rights\s+reserved).*?\b', '', text)
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
                    "sourceName": article.get("source", "Finnhub API"),
                    "imageUrl": article.get("image", "")
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
            
            pubDate_m = re.search(r'<pubDate>(.*?)</pubDate>', cb, re.DOTALL | re.IGNORECASE)
            pub_time = None
            if pubDate_m:
                import email.utils
                try:
                    parsed_tuple = email.utils.parsedate_tz(pubDate_m.group(1))
                    if parsed_tuple:
                        pub_time = email.utils.mktime_tz(parsed_tuple)
                except:
                    pass
            
            # Extract image
            img_m = re.search(r"<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\'\"]([^\'\"]+)[\'\"]", cb, re.IGNORECASE)
            image_url = img_m.group(1) if img_m else None
            
            # Try parsing from description if not found
            if not image_url and desc_m:
                img_desc = re.search(r"<img[^>]*src=[\'\"]([^\'\"]+)[\'\"]", desc_m.group(1), re.IGNORECASE)
                if img_desc:
                    image_url = img_desc.group(1)
            
            if title and link:
                clean_link = link.split("?")[0]
                items.append({
                    "title": title[:250],
                    "url": clean_link,
                    "text": desc[:1000] if len(desc) > 10 else title,
                    "category": feed_info["category"],
                    "sourceName": feed_info["sourceName"],
                    "imageUrl": image_url,
                    "publishedAt": int(pub_time * 1000) if pub_time else None
                })
    except Exception as e:
        logging.warning(f"Error reading feed {feed_info['url']}: {e}")
    return items

def call_gemini_batch_api(items: list) -> dict:
    if not GEMINI_API_KEY or "YOUR_" in GEMINI_API_KEY:
        logging.warning("No valid GEMINI_API_KEY found. Utilizing local NLP fallback.")
        return {}

    simplified = [{"id": item["id"], "title": item["title"], "content": item["text"]} for item in items]
    simplified_json = json.dumps(simplified, indent=2)
    
    prompt = f"""You are the Automated Financial Tech & News Scraper Engine.
Analyze these news items and produce structured JSON output.

Classify the scraped RSS articles into EXACTLY ONE of the following 5 niche target topics:
FREELANCER_REMOTE_FINANCE, ESOP_STARTUP_EQUITY, SME_D2C_FINTECH, FIRE_HIGH_INCOME_TECH, WEB3_ALTERNATIVE_ASSETS

Output MUST be strictly valid JSON without markdown code blocks.

Respond ONLY with a JSON Array of objects matching this exact format for each item:
[
  {{
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
  }}
]

Items to analyze:
{simplified_json}
"""

    url_primary = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key={GEMINI_API_KEY}"
    url_fallback = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={GEMINI_API_KEY}"
    
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {"response_mime_type": "application/json"}
    }

    for target_url in [url_primary, url_fallback]:
        try:
            req = urllib.request.Request(
                target_url,
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
                logging.warning("Rate limited (429). Trying fallback URL or NLP.")
                continue
            else:
                logging.error(f"Gemini API HTTP Error ({target_url}): {e}")
                continue
        except Exception as e:
            logging.error(f"Gemini API call failed: {e}")
            continue
            
    return {}

def generate_fallback_llm_summary(item: dict) -> dict:
    title = item["title"]
    category = item["category"]
    raw_text = item.get("text", title)
    
    # Extract clean actual sentences from article text
    clean_text = clean_html_text(raw_text)
    raw_sentences = [
        s.strip() for s in re.split(r'[.!?]+', clean_text)
        if len(s.strip()) > 15 and not re.search(r'(?i)\b(published\s+by|home\b|copyright|all\s+rights\s+reserved|click\s+here|read\s+more)\b', s)
    ]
    
    # Avoid duplicate sentences matching title
    sentences = [s for s in raw_sentences if s.lower() not in title.lower()]
    if not sentences:
        sentences = [title]
        
    # Build 6 to 7 line narrative paragraph (~6-7 sentences)
    summary_sentences = sentences[:7]
    summary = ". ".join(summary_sentences)
    summary = re.sub(r'\.\s*\.', '.', summary)
    if not summary.endswith("."):
        summary += "."

    return {
        "id": item["id"],
        "title": title[:250],
        "summary": summary[:1500],
        "who_impacted": sentences[1] if len(sentences) > 1 else f"Readers and consumers following {category}",
        "reason": sentences[2] if len(sentences) > 2 else title,
        "financial_impact": sentences[3] if len(sentences) > 3 else "",
        "action": sentences[4] if len(sentences) > 4 else "",
        "category": category,
        "topic_cluster": f"{category} Update"
    }
def purge_all_supabase_news():
    if not SUPABASE_URL or not SUPABASE_KEY or "YOUR_" in SUPABASE_URL:
        logging.info("Supabase purge skipped (credentials not set).")
        return

    url = f"{SUPABASE_URL.rstrip('/')}/rest/v1/financial_news?publishedAt=gt.0"
    headers = {
        'apikey': SUPABASE_KEY,
        'Authorization': f'Bearer {SUPABASE_KEY}'
    }
    try:
        req = urllib.request.Request(url, headers=headers, method='DELETE')
        with urllib.request.urlopen(req, timeout=10) as resp:
            logging.info(f"Supabase news table purged successfully! Status: {resp.status}")
    except Exception as e:
        logging.warning(f"Could not purge Supabase news table: {e}")

def fetch_existing_supabase_urls() -> set:
    if not SUPABASE_URL or not SUPABASE_KEY or "YOUR_" in SUPABASE_URL:
        return set()
    url = f"{SUPABASE_URL.rstrip('/')}/rest/v1/financial_news?select=sourceUrl&limit=500"
    headers = {
        'apikey': SUPABASE_KEY,
        'Authorization': f'Bearer {SUPABASE_KEY}'
    }
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=10) as resp:
            data = json.loads(resp.read().decode('utf-8'))
            return {item["sourceUrl"] for item in data if isinstance(item, dict) and "sourceUrl" in item and item["sourceUrl"]}
    except Exception as e:
        logging.warning(f"Could not fetch existing Supabase URLs for deduplication: {e}")
        return set()

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
            "summaryWhatHappened": llm.get("summary", "")[:1500],
            "summaryWhoImpacted": llm.get("who_impacted", "")[:500],
            "summaryActionableTakeaway": llm.get("action", "")[:500],
            "summaryText": llm.get("reason", "")[:1500],
            "category": r["category"][:50],
            "financialActionUrl": r["url"],
            "sourceUrl": r["url"],
            "sourceName": r["sourceName"][:90],
            "imageUrl": r.get("imageUrl"),
            "financialImpactBullets": llm.get("financial_impact", "")[:1500],
            "publishedAt": r.get("publishedAt") or now_ms
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

    # Purge existing news from Supabase database to remove outdated or unwanted news
    purge_all_supabase_news()

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

    # Deduplicate against existing Supabase database to save Gemini API costs
    existing_urls = fetch_existing_supabase_urls()
    logging.info(f"Found {len(existing_urls)} existing articles in Supabase database.")

    items_to_process = [item for item in raw_items if item["url"] not in existing_urls]
    logging.info(f"New articles needing Gemini LLM processing: {len(items_to_process)} (Saved {len(raw_items) - len(items_to_process)} LLM API calls!)")

    # Call Gemini in batches of 15 for new articles only
    llm_map = {}
    batch_size = 15
    for i in range(0, len(items_to_process), batch_size):
        batch = items_to_process[i:i+batch_size]
        batch_map = call_gemini_batch_api(batch)
        llm_map.update(batch_map)
        if i + batch_size < len(items_to_process):
            time.sleep(5)

    processed_list = []
    for item in raw_items:
        summary_obj = llm_map.get(item["id"], generate_fallback_llm_summary(item))
        raw_cat = summary_obj.get("category", item["category"])
        cat = detect_category(summary_obj.get("title", item["title"]), item["text"], raw_cat)
        
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
                "topic_cluster": summary_obj.get("monetization_angle", "")
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
