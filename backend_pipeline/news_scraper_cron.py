import os
import json
import time
import logging
import requests
import feedparser
from bs4 import BeautifulSoup
from google import genai
from supabase import create_client, Client
from googleapiclient.discovery import build

# Configure Logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# Environment Variables (Set these in your Serverless/Cron environment)
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY")
SUPABASE_URL = os.getenv("SUPABASE_URL", "YOUR_SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY", "YOUR_SUPABASE_SERVICE_ROLE_KEY")
YOUTUBE_API_KEY = os.getenv("YOUTUBE_API_KEY", "YOUR_YOUTUBE_API_KEY")

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
RAW_DATA_FILE = os.path.join(BASE_DIR, "raw_scraped_data.json")
PROCESSED_DATA_FILE = os.path.join(BASE_DIR, "processed_scraped_data.json")

# Initialize Gemini
gemini_client = None
try:
    if GEMINI_API_KEY and "YOUR_" not in GEMINI_API_KEY:
        gemini_client = genai.Client(api_key=GEMINI_API_KEY)
        logging.info("Gemini client initialized successfully.")
    else:
        logging.warning("GEMINI_API_KEY not set or invalid placeholder.")
except Exception as e:
    logging.error(f"Error configuring Gemini API: {e}")

# Initialize Supabase safely
supabase: Client = None
try:
    if (SUPABASE_URL and SUPABASE_URL.startswith("http") and "YOUR_" not in SUPABASE_URL and
        SUPABASE_KEY and len(SUPABASE_KEY) > 20 and "YOUR_" not in SUPABASE_KEY):
        supabase = create_client(SUPABASE_URL, SUPABASE_KEY)
        logging.info("Supabase client initialized successfully.")
    else:
        logging.warning("Supabase URL or Key missing or placeholder. Skipping DB connection.")
except Exception as e:
    logging.warning(f"Failed to initialize Supabase client (check your SUPABASE_URL and SUPABASE_KEY secrets): {e}")
    supabase = None

# Initialize YouTube Client safely
youtube = None
try:
    if YOUTUBE_API_KEY and "YOUR_" not in YOUTUBE_API_KEY:
        youtube = build('youtube', 'v3', developerKey=YOUTUBE_API_KEY)
        logging.info("YouTube client initialized successfully.")
    else:
        logging.warning("YouTube API Key not set/invalid. Skipping YouTube connection.")
except Exception as e:
    logging.warning(f"Failed to initialize YouTube client: {e}")
    youtube = None

def fetch_financial_news():
    """
    Scrapes the latest financial news from RSS feeds.
    Fetches up to 10 articles per category.
    """
    logging.info("Scraping financial news...")
    
    feeds = [
        {"category": "Stock Market India", "url": "https://economictimes.indiatimes.com/markets/rssfeeds/2146842.cms"},
        {"category": "Credit Cards", "url": "https://www.livemint.com/rss/money"},
        {"category": "ITR & Tax", "url": "https://economictimes.indiatimes.com/wealth/tax/rssfeeds/83755913.cms"},
        {"category": "Markets & Mutual Funds", "url": "https://economictimes.indiatimes.com/mf/rssfeeds/83756208.cms"}
    ]
    
    articles = []
    item_id = 1
    for feed in feeds:
        try:
            parsed = feedparser.parse(feed["url"])
            count = 0
            for entry in parsed.entries:
                if count >= 10:
                    break
                    
                summary_html = entry.get("summary", "")
                soup = BeautifulSoup(summary_html, 'html.parser')
                clean_text = soup.get_text(strip=True)
                
                if not clean_text:
                    clean_text = entry.get("title", "")
                
                articles.append({
                    "id": item_id,
                    "is_video": False,
                    "title": entry.get("title", ""),
                    "url": entry.get("link", ""),
                    "text": clean_text,
                    "category": feed["category"],
                    "sourceName": "Indian Financial News Feed",
                    "imageUrl": None
                })
                item_id += 1
                count += 1
            logging.info(f"Fetched {count} articles for category: {feed['category']}")
        except Exception as e:
            logging.error(f"Error fetching RSS feed {feed['url']}: {e}")
            
    return articles, item_id

def fetch_youtube_shorts(start_id: int):
    """
    Fetches the latest YouTube Shorts or videos related to Indian Finance.
    """
    logging.info("Fetching YouTube Finance Videos...")
    if not youtube:
        logging.warning("YouTube API client not initialized. Skipping fetch.")
        return []
        
    categories = [
        {"category": "Stock Market India", "query": "Indian stock market Sensex Nifty share market news 2026"},
        {"category": "ITR & Tax", "query": "Indian income tax ITR latest"},
        {"category": "Credit Cards", "query": "Indian credit cards tips 2026"},
    ]
    
    videos = []
    item_id = start_id
    for cat in categories:
        try:
            request = youtube.search().list(
                part="snippet",
                q=cat["query"],
                maxResults=10,
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
                    "category": "Video Shorts",
                    "imageUrl": snippet["thumbnails"]["high"]["url"]
                })
                item_id += 1
        except Exception as e:
            logging.error(f"Error fetching YouTube videos for {cat['category']}: {e}")
            
    return videos

def summarize_batch_with_gemini(items: list) -> dict:
    """
    Summarizes a batch of items in ONE single Gemini API call.
    Returns a dictionary mapping item_id -> LLM summary JSON.
    """
    if not items:
        return {}
        
    if not gemini_client:
        logging.warning("Gemini client not initialized. Skipping summarization.")
        return {}

    logging.info(f"Sending batch of {len(items)} items to Gemini in a SINGLE API call...")

    simplified_items = [
        {
            "id": item["id"],
            "title": item["title"],
            "content": item["text"][:1500]  # Trim content to manage context efficiently
        }
        for item in items
    ]

    prompt = f"""You are an expert financial news summarizer. Process the following list of news/video items and summarize EACH item into JSON format.

    Input Items:
    {json.dumps(simplified_items, indent=2)}

    Respond ONLY with a JSON Array containing an object for each item with these exact keys:
    [
      {{
        "id": <number matching input id>,
        "summary": "Detailed 6 to 8-line summary of the news in English without prefixes like 'What happened:'.",
        "reason": "4 to 5 lines explaining why this decision/action was taken.",
        "financial_impact": "Financial impact or benefits for taxpayers/investors in 3 to 4 lines with numbers.",
        "action": "Actionable steps in 3 to 4 lines.",
        "category": "One of: Stock Market India, ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"
      }}
    ]
    """

    try:
        response = gemini_client.models.generate_content(
            model='gemini-3.5-flash',
            contents=prompt,
        )
        
        response_text = response.text.strip()
        if response_text.startswith("```json"):
            response_text = response_text[7:]
        if response_text.startswith("```"):
            response_text = response_text[3:]
        if response_text.endswith("```"):
            response_text = response_text[:-3]
        response_text = response_text.strip()

        parsed_data = json.loads(response_text)
        
        # Handle case where model returns a dict with an 'items' key instead of a raw list
        if isinstance(parsed_data, dict):
            parsed_list = parsed_data.get("items", parsed_data.get("data", [parsed_data]))
        elif isinstance(parsed_data, list):
            parsed_list = parsed_data
        else:
            parsed_list = []

        summaries_by_id = {}
        for summary in parsed_list:
            if isinstance(summary, dict) and "id" in summary:
                try:
                    item_id = int(summary["id"])
                    summaries_by_id[item_id] = summary
                except (ValueError, TypeError):
                    pass

        logging.info(f"Gemini successfully summarized {len(summaries_by_id)} items in 1 API call.")
        return summaries_by_id

    except Exception as e:
        logging.error(f"Failed to perform batch summarization with Gemini: {e}")
        return {}

def push_batch_to_supabase(payloads: list):
    """
    Inserts a list of formatted records into Supabase in bulk.
    Falls back to row-by-row insertion if a batch error occurs.
    """
    if not payloads:
        logging.info("No payloads to insert into Supabase.")
        return

    if not supabase:
        logging.warning("Supabase client not initialized. Skipping database insertion.")
        return

    try:
        supabase.table("financial_news").insert(payloads).execute()
        logging.info(f"Successfully inserted batch of {len(payloads)} records into Supabase.")
    except Exception as e:
        logging.error(f"Batch insert error: {e}. Falling back to row-by-row insertion...")
        inserted_count = 0
        for payload in payloads:
            try:
                supabase.table("financial_news").insert(payload).execute()
                inserted_count += 1
            except Exception as item_err:
                logging.error(f"Failed to insert item '{payload.get('title')}': {item_err}")
        logging.info(f"Row-by-row fallback completed. Inserted {inserted_count}/{len(payloads)} records.")

def cleanup_old_news(days: int = 15):
    """
    Deletes records from Supabase financial_news table that are older than 'days' (default 15 days).
    """
    if not supabase:
        logging.warning("Supabase client not initialized. Skipping database cleanup.")
        return

    try:
        # Calculate cutoff timestamp in milliseconds (epoch ms)
        cutoff_ms = int((time.time() - (days * 24 * 60 * 60)) * 1000)
        logging.info(f"Cleaning up financial_news items older than {days} days (publishedAt < {cutoff_ms})...")
        
        response = supabase.table("financial_news").delete().lt("publishedAt", cutoff_ms).execute()
        logging.info(f"Cleanup operation executed successfully: {response}")
    except Exception as e:
        logging.error(f"Error during Supabase cleanup operation: {e}")

def main():
    logging.info("Starting unified optimized background scraping pipeline...")

    # 1. Fetch raw items from RSS feeds and YouTube
    articles, next_id = fetch_financial_news()
    videos = fetch_youtube_shorts(start_id=next_id)
    all_raw_items = articles + videos

    logging.info(f"Total raw items collected: {len(all_raw_items)}")

    # 2. Store raw items locally in GitHub server/runner filesystem
    try:
        with open(RAW_DATA_FILE, "w", encoding="utf-8") as f:
            json.dump(all_raw_items, f, indent=2, ensure_ascii=False)
        logging.info(f"Saved raw scraped data locally to '{RAW_DATA_FILE}'.")
    except Exception as e:
        logging.error(f"Failed to save local raw data file: {e}")

    # 3. Process items with ONE batched Gemini API call
    # If there are many items, process in chunks of 20 to fit prompt context smoothly
    CHUNK_SIZE = 20
    summaries_map = {}
    for i in range(0, len(all_raw_items), CHUNK_SIZE):
        chunk = all_raw_items[i : i + CHUNK_SIZE]
        chunk_summaries = summarize_batch_with_gemini(chunk)
        summaries_map.update(chunk_summaries)

    # 4. Construct final payload & store processed data locally
    now_ms = int(time.time() * 1000)
    payloads = []
    processed_items = []

    for item in all_raw_items:
        item_id = item["id"]
        llm_data = summaries_map.get(item_id, {})
        
        # Skip if Gemini summary failed for this item
        if not llm_data:
            continue

        payload = {
            "title": item["title"],
            "sourceUrl": item["url"],
            "summaryWhatHappened": llm_data.get("summary", ""),
            "summaryText": llm_data.get("reason", ""),
            "summaryActionableTakeaway": llm_data.get("action", ""),
            "financialImpactBullets": llm_data.get("financial_impact", ""),
            "category": "Video Shorts" if item.get("is_video") else item.get("category", llm_data.get("category", "Stock Market India")),
            "sourceName": item.get("sourceName", "Indian Financial News Feed"),
            "imageUrl": item.get("imageUrl", None),
            "publishedAt": now_ms
        }
        payloads.append(payload)

        processed_item = dict(item)
        processed_item["llm_summary"] = llm_data
        processed_items.append(processed_item)

    try:
        with open(PROCESSED_DATA_FILE, "w", encoding="utf-8") as f:
            json.dump(processed_items, f, indent=2, ensure_ascii=False)
        logging.info(f"Saved processed data locally to '{PROCESSED_DATA_FILE}'.")
    except Exception as e:
        logging.error(f"Failed to save local processed data file: {e}")

    # 5. Push batch payloads into Supabase
    push_batch_to_supabase(payloads)

    # 6. Delete news items older than 15 days in Supabase
    cleanup_old_news(days=15)

    logging.info("Pipeline execution completed successfully.")

if __name__ == "__main__":
    main()

