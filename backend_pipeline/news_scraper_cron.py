import os
import json
import logging
import requests
from bs4 import BeautifulSoup
import google.generativeai as genai
from supabase import create_client, Client
from googleapiclient.discovery import build

# Configure Logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# Environment Variables (Set these in your Serverless/Cron environment)
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY")
SUPABASE_URL = os.getenv("SUPABASE_URL", "YOUR_SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY", "YOUR_SUPABASE_SERVICE_ROLE_KEY")
YOUTUBE_API_KEY = os.getenv("YOUTUBE_API_KEY", "YOUR_YOUTUBE_API_KEY")

# Initialize Clients
genai.configure(api_key=GEMINI_API_KEY)
supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)
youtube = build('youtube', 'v3', developerKey=YOUTUBE_API_KEY)

def fetch_financial_news():
    """
    Scrapes the latest financial news from a public portal (Example: moneycontrol/economictimes).
    """
    logging.info("Scraping financial news...")
    # MOCK implementation
    return [
        {
            "title": "RBI mandates new credit card billing cycle rules",
            "url": "https://www.rbi.org.in/scripts/NotificationUser.aspx",
            "text": "The Reserve Bank of India has announced new guidelines allowing credit card users to modify their billing cycles multiple times to align with their salary dates, effectively helping them manage cash flows better."
        }
    ]

def fetch_youtube_shorts():
    """
    Fetches the latest YouTube Shorts or videos related to Indian Finance.
    """
    logging.info("Fetching YouTube Finance Videos...")
    categories = [
        {"category": "ITR & Tax", "query": "Indian income tax ITR latest"},
        {"category": "Credit Cards", "query": "Indian credit cards tips 2026"},
    ]
    
    videos = []
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
                    "title": snippet["title"],
                    "url": f"https://www.youtube.com/watch?v={item['id']['videoId']}",
                    "text": snippet["description"], # We can pass description to Gemini
                    "channel": snippet["channelTitle"],
                    "category": cat["category"],
                    "imageUrl": snippet["thumbnails"]["high"]["url"]
                })
        except Exception as e:
            logging.error(f"Error fetching YouTube videos for {cat['category']}: {e}")
            
    return videos

def summarize_with_gemini(raw_text: str) -> dict:
    prompt = f"""You are an expert financial news summarizer. Extract and structure the following news into this exact JSON format. Keep it concise. All output MUST be in English.
News: {raw_text}

Respond ONLY with JSON:
{{
    "summary": "Provide a 6 to 7-line summary of the news in English. Do NOT include prefixes like 'What happened:'.",
    "impacted_users": "Provide 2 to 3 lines explaining who are the users impacted in English. Do NOT include prefixes like 'Who is impacted:'.",
    "reason": "Provide 2 to 3 lines explaining why the government or entity has taken this decision in English. Do NOT include prefixes like 'Reason:'.",
    "financial_impact": "What is the financial impact or the benefits users can gain in English? Use crisp, quantifiable numbers and bullet points.",
    "action": "Provide actionable steps (2 to 3 lines) users should take in English. Do NOT include prefixes like 'Actionable Takeaway:' or 'Action:'.",
    "category": "One of: ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"
}}"""

    model = genai.GenerativeModel('gemini-1.5-pro')
    response = model.generate_content(prompt)
    
    response_text = response.text.strip()
    if response_text.startswith("```json"):
        response_text = response_text[7:]
    if response_text.endswith("```"):
        response_text = response_text[:-3]
        
    try:
        return json.loads(response_text)
    except Exception as e:
        logging.error(f"Failed to parse LLM response: {e}")
        return {}

def push_to_supabase(article_data: dict, llm_data: dict, is_video: bool = False):
    if not llm_data:
        return
        
    payload = {
        "title": article_data["title"],
        "sourceUrl": article_data["url"],
        "summaryWhatHappened": llm_data.get("summary", ""),
        "summaryWhoImpacted": llm_data.get("impacted_users", ""),
        "summaryText": llm_data.get("reason", ""),
        "summaryActionableTakeaway": llm_data.get("action", ""),
        "financialImpactBullets": llm_data.get("financial_impact", ""),
        "category": article_data.get("category", llm_data.get("category", "ITR & Tax")),
        "sourceName": article_data.get("channel", "Indian Financial News Feed"),
        "imageUrl": article_data.get("imageUrl", None),
        "publishedAt": "now()"
    }
    
    if is_video:
        payload["category"] = "Video Shorts"
    
    try:
        supabase.table("financial_news").insert(payload).execute()
        logging.info(f"Successfully inserted: {article_data['title']}")
    except Exception as e:
        logging.error(f"Error inserting into Supabase: {e}")

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
        llm_data = summarize_with_gemini(video["text"]) # Or pass title + desc
        push_to_supabase(video, llm_data, is_video=True)
        
    logging.info("Pipeline execution completed.")

if __name__ == "__main__":
    main()
