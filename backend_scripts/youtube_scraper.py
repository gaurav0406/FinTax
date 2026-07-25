import os
import requests
from supabase import create_client, Client
from datetime import datetime
import time

# Environment Variables
YOUTUBE_API_KEY = os.environ.get("YOUTUBE_API_KEY")
SUPABASE_URL = os.environ.get("SUPABASE_URL")
SUPABASE_KEY = os.environ.get("SUPABASE_KEY")

def fetch_youtube_videos(query):
    url = "https://www.googleapis.com/youtube/v3/search"
    params = {
        "part": "snippet",
        "q": query,
        "type": "video",
        "maxResults": 10,
        "key": YOUTUBE_API_KEY
    }
    response = requests.get(url, params=params)
    response.raise_for_status()
    return response.json().get("items", [])

def process_and_upload():
    if not all([YOUTUBE_API_KEY, SUPABASE_URL, SUPABASE_KEY]):
        print("Error: Missing environment variables.")
        return

    supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)
    
    categories = [
        ("ITR & Tax", "Indian income tax ITR"),
        ("Credit Cards", "Indian credit cards best"),
        ("Loans & FDs", "Indian home loans fixed deposits"),
        ("Markets & Mutual Funds", "Indian stock market mutual funds"),
        ("RBI & Policy", "RBI monetary policy updates")
    ]
    
    all_records = []
    
    for cat_name, query in categories:
        print(f"Fetching videos for: {cat_name}...")
        items = fetch_youtube_videos(query)
        
        for item in items:
            video_id = item["id"].get("videoId")
            snippet = item.get("snippet", {})
            if not video_id: 
                continue

            # Map to FinancialNewsEntity schema
            all_records.append({
                "title": snippet.get("title", "Finance Video"),
                "summaryWhatHappened": snippet.get("description", "")[:100],
                "summaryWhoImpacted": snippet.get("channelTitle", "YouTube"),
                "summaryActionableTakeaway": "Watch this video for financial insights.",
                "summaryText": snippet.get("description", ""),
                "category": cat_name, 
                "sourceUrl": f"https://www.youtube.com/watch?v={video_id}",
                "sourceName": snippet.get("channelTitle", "YouTube"),
                "imageUrl": snippet.get("thumbnails", {}).get("high", {}).get("url", ""),
                "financialImpactBullets": f"• Key Highlight: Video insight regarding {cat_name}.",
                "publishedAt": int(time.time() * 1000)
            })

    if all_records:
        print(f"Uploading {len(all_records)} video records to Supabase...")
        # Assuming your table is named 'financial_news'
        response = supabase.table("financial_news").upsert(all_records).execute()
        print("Upload complete!")

if __name__ == "__main__":
    process_and_upload()
