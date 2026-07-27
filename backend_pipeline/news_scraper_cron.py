import os
import json
import logging
import requests
import feedparser
from bs4 import BeautifulSoup
import google.generativeai as genai
from googleapiclient.discovery import build

# Configure Logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# Environment Variables (Set these in your Serverless/Cron environment)
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY")
SUPABASE_URL = os.getenv("SUPABASE_URL", "YOUR_SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY", "YOUR_SUPABASE_SERVICE_ROLE_KEY")
YOUTUBE_API_KEY = os.getenv("YOUTUBE_API_KEY", "YOUR_YOUTUBE_API_KEY")

# Initialize Gemini
try:
    if GEMINI_API_KEY and "YOUR_" not in GEMINI_API_KEY:
        genai.configure(api_key=GEMINI_API_KEY)
    else:
        logging.warning("GEMINI_API_KEY not set or invalid placeholder.")
except Exception as e:
    logging.error(f"Error configuring Gemini API: {e}")

# Check Supabase Credentials
if (SUPABASE_URL and SUPABASE_URL.startswith("http") and "YOUR_" not in SUPABASE_URL and
    SUPABASE_KEY and len(SUPABASE_KEY) > 20 and "YOUR_" not in SUPABASE_KEY):
    logging.info("Supabase configuration found. Using REST API for insertions.")
    USE_SUPABASE = True
else:
    logging.warning("Supabase URL or Key missing or placeholder. Skipping DB connection.")
    USE_SUPABASE = False

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
    Fetches 10-15 articles per category.
    """
    logging.info("Scraping financial news...")
    
    # We will use public RSS feeds for financial news (e.g., Economic Times & Livemint)
    feeds = [
        {"category": "Stock Market India", "url": "https://economictimes.indiatimes.com/markets/rssfeeds/2146842.cms"},
        {"category": "Credit Cards", "url": "https://www.livemint.com/rss/money"},
        {"category": "ITR & Tax", "url": "https://economictimes.indiatimes.com/wealth/tax/rssfeeds/83755913.cms"},
        {"category": "Markets & Mutual Funds", "url": "https://economictimes.indiatimes.com/mf/rssfeeds/83756208.cms"}
    ]
    
    articles = []
    for feed in feeds:
        try:
            parsed = feedparser.parse(feed["url"])
            count = 0
            for entry in parsed.entries:
                if count >= 15:
                    break
                    
                summary_html = entry.get("summary", "")
                soup = BeautifulSoup(summary_html, 'html.parser')
                clean_text = soup.get_text(strip=True)
                
                # Fallback to title if summary is empty
                if not clean_text:
                    clean_text = entry.get("title", "")
                
                articles.append({
                    "title": entry.get("title", ""),
                    "url": entry.get("link", ""),
                    "text": clean_text,
                    "category": feed["category"]
                })
                count += 1
            logging.info(f"Fetched {count} articles for category: {feed['category']}")
        except Exception as e:
            logging.error(f"Error fetching RSS feed {feed['url']}: {e}")
            
    return articles

def fetch_youtube_shorts():
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
    for cat in categories:
        try:
            request = youtube.search().list(
                part="snippet",
                q=cat["query"],
                maxResults=15,
                type="video"
            )
            response = request.execute()
            
            for item in response.get("items", []):
                snippet = item["snippet"]
                videos.append({
                    "title": snippet["title"],
                    "url": f"https://www.youtube.com/watch?v={item['id']['videoId']}",
                    "text": snippet["description"], # Pass description to Gemini
                    "channel": snippet["channelTitle"],
                    "category": cat["category"],
                    "imageUrl": snippet["thumbnails"]["high"]["url"]
                })
        except Exception as e:
            logging.error(f"Error fetching YouTube videos for {cat['category']}: {e}")
            
    return videos

def summarize_with_gemini(raw_text: str) -> dict:
    prompt = f"""You are an expert financial news summarizer. Extract and structure the following news into this exact JSON format. Keep it concise, but ensure the content is very insightful and useful. All output MUST be in English.

News: {raw_text}

Respond ONLY with JSON:
{{
    "summary": "Provide a detailed 7 to 8-line summary of the news in English. Do NOT include prefixes like 'What happened:'.",
    "reason": "Provide 4 to 5 lines explaining why the government, entity, or individual has taken this decision/action in English. Do NOT include prefixes like 'Reason:'.",
    "financial_impact": "What is the financial impact or the benefits users like TaxPayers, Investors, and other users can gain in English? Use 3 to 4 lines. Use crisp, quantifiable numbers and bullet points.",
    "action": "Provide actionable steps (3 to 4 lines) a user or company should take based on this news in English. Do NOT include prefixes like 'Actionable Takeaway:' or 'Action:'.",
    "category": "One of: Stock Market India, ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"
}}"""
    try:
        # Using a reliable model name available for developers. 
        # Fallback to flash-8b or standard flash if needed
        model = genai.GenerativeModel('gemini-1.5-flash')
        response = model.generate_content(prompt)
        
        response_text = response.text.strip()
        if response_text.startswith("```json"):
            response_text = response_text[7:]
        if response_text.endswith("```"):
            response_text = response_text[:-3]
            
        return json.loads(response_text)
    except Exception as e:
        logging.error(f"Failed to summarize with Gemini: {e}")
        return {}

def push_to_supabase(article_data: dict, llm_data: dict, is_video: bool = False):
    if not llm_data:
        return
        
    payload = {
        "title": article_data["title"],
        "sourceUrl": article_data["url"],
        "summaryWhatHappened": llm_data.get("summary", ""),
        "summaryText": llm_data.get("reason", ""),
        "summaryActionableTakeaway": llm_data.get("action", ""),
        "financialImpactBullets": llm_data.get("financial_impact", ""),
        "category": article_data.get("category", llm_data.get("category", "Stock Market India")),
        "sourceName": article_data.get("channel", "Indian Financial News Feed"),
        "imageUrl": article_data.get("imageUrl", None),
        "publishedAt": "now()"
    }
    
    if is_video:
        payload["category"] = "Video Shorts"
        
    if not USE_SUPABASE:
        logging.warning("Supabase client not initialized. Skipping database insertion for: " + article_data["title"])
        return
        
    try:
        headers = {
            "apikey": SUPABASE_KEY,
            "Authorization": f"Bearer {SUPABASE_KEY}",
            "Content-Type": "application/json",
            "Prefer": "return=minimal"
        }
        # Using REST API call to completely bypass the python supabase client proxy issue
        endpoint = f"{SUPABASE_URL.rstrip('/')}/rest/v1/financial_news"
        response = requests.post(endpoint, headers=headers, json=payload)
        
        if response.status_code in [200, 201, 204]:
            logging.info(f"Successfully inserted: {article_data['title']}")
        else:
            logging.error(f"Failed to insert into Supabase: {response.status_code} - {response.text}")
    except Exception as e:
        logging.error(f"Error inserting into Supabase via REST: {e}")

def main():
    logging.info("Starting unified background scraping pipeline...")
    
    # 1. Process News Articles
    articles = fetch_financial_news()
    for article in articles:
        logging.info(f"Processing News: {article['title']}")
        llm_data = summarize_with_gemini(article["text"])
        push_to_supabase(article, llm_data)
        
    # 2. Process YouTube Videos
    videos = fetch_youtube_shorts()
    for video in videos:
        logging.info(f"Processing Video: {video['title']}")
        video_context = f"Video Title: {video['title']}\nVideo Description: {video['text']}"
        llm_data = summarize_with_gemini(video_context)
        push_to_supabase(video, llm_data, is_video=True)
        
    logging.info("Pipeline execution completed.")

if __name__ == "__main__":
    main()
