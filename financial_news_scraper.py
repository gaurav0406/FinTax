#!/usr/bin/env python3
"""
FinTax Audio News - Production-Ready Indian Financial NLP & Web Scraping Pipeline
Author: Lead Python & NLP Engineer
Description: Automated pipeline that scrapes RSS feeds from Indian financial outlets,
processes news content using Gemini AI into structured JSON summaries tailored for taxpayers,
converts the summary to speech via gTTS, and persists records & audio URLs to Supabase.
"""

import os
import sys
import time
import json
import logging
import hashlib
import re
from typing import List, Dict, Any, Optional
import feedparser
import requests
from bs4 import BeautifulSoup
import google.generativeai as genai
from gtts import gTTS
from supabase import create_client, Client

# Configure Logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger("FinTaxPipeline")

# Configuration & Environment Variables
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")
SUPABASE_URL = os.getenv("SUPABASE_URL", "")
SUPABASE_KEY = os.getenv("SUPABASE_SERVICE_KEY", os.getenv("SUPABASE_KEY", ""))
AUDIO_OUTPUT_DIR = os.getenv("AUDIO_OUTPUT_DIR", "./audio")
AUDIO_STORAGE_BUCKET = os.getenv("AUDIO_STORAGE_BUCKET", "financial_news_audio")

# Indian Financial RSS Feeds
INDIAN_FINANCIAL_FEEDS = [
    {
        "name": "Economic Times Wealth",
        "url": "https://economictimes.indiatimes.com/wealth/rssfeeds/1254212.cms",
        "default_category": "ITR & Tax"
    },
    {
        "name": "LiveMint Money",
        "url": "https://www.livemint.com/rss/money",
        "default_category": "Markets & Mutual Funds"
    },
    {
        "name": "Moneycontrol Personal Finance",
        "url": "https://www.moneycontrol.com/rss/personalfinance.xml",
        "default_category": "Loans & FDs"
    },
    {
        "name": "RBI Press Releases",
        "url": "https://rbi.org.in/rssfeed.xml",
        "default_category": "RBI & Policy"
    }
]

# System Prompt for Gemini Structured JSON Output
SYSTEM_PROMPT = """
You are an expert Indian Financial NLP & Tax Journalist.
Your task is to analyze raw news articles and produce structured, actionable intelligence specifically for Indian taxpayers and retail investors.

Output MUST be strictly valid JSON without any markdown codeblock formatting.

Required JSON Structure:
{
  "title": "Catchy headline tailored for Indian taxpayers/investors (Max 10 words)",
  "summary": [
    "Point 1: What happened (1-2 sentences)",
    "Point 2: Who is impacted e.g. Salaried Class, Senior Citizens, Taxpayers (1 sentence)",
    "Point 3: Actionable Takeaway e.g. File ITR-1 before July 31, Link Aadhaar (1 sentence)"
  ],
  "category": "Must be EXACTLY ONE of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy']",
  "financial_action_url": "Optional affiliate or official portal URL (e.g. incometax.gov.in, rbi.org.in, or null)",
  "source_url": "Original article link"
}

Constraints:
1. 'title' must be concise and engaging (Maximum 10 words).
2. 'summary' must contain EXACTLY 3 bullet points.
3. The total word count of all 3 bullet points combined MUST NOT exceed 60 words.
4. 'category' must strictly match one of the 5 allowed values.
5. Content must focus on practical financial implications for Indians (Section 80C, Income Tax Slabs, Repo Rate, FD Rates, Credit Card Rewards, Mutual Fund NAV).
"""

class FinancialNewsScraperPipeline:
    def __init__(self):
        self._init_gemini()
        self.supabase: Optional[Client] = self._init_supabase()
        os.makedirs(AUDIO_OUTPUT_DIR, exist_ok=True)

    def _init_gemini(self):
        if not GEMINI_API_KEY:
            logger.warning("GEMINI_API_KEY is missing! Set GEMINI_API_KEY environment variable.")
            return
        genai.configure(api_key=GEMINI_API_KEY)
        logger.info("Gemini AI API configured successfully.")

    def _init_supabase(self) -> Optional[Client]:
        if not SUPABASE_URL or not SUPABASE_KEY:
            logger.warning("SUPABASE_URL or SUPABASE_KEY missing. Database saving will be simulated locally.")
            return None
        try:
            client = create_client(SUPABASE_URL, SUPABASE_KEY)
            logger.info("Supabase PostgreSQL client connected.")
            return client
        except Exception as e:
            logger.error(f"Failed to connect to Supabase: {e}")
            return None

    def clean_html(self, raw_html: str) -> str:
        """Removes HTML tags and cleans text content."""
        if not raw_html:
            return ""
        soup = BeautifulSoup(raw_html, "html.parser")
        text = soup.get_text(separator=" ", strip=True)
        return re.sub(r'\s+', ' ', text)

    def scrape_article_body(self, url: str) -> str:
        """Fetches and extracts raw text content from the news URL."""
        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        }
        try:
            response = requests.get(url, headers=headers, timeout=10)
            if response.status_code == 200:
                soup = BeautifulSoup(response.text, "html.parser")
                for element in soup(["script", "style", "nav", "header", "footer"]):
                    element.decompose()
                paragraphs = soup.find_all("p")
                article_text = " ".join([p.get_text(strip=True) for p in paragraphs if len(p.get_text(strip=True)) > 20])
                return article_text[:3000]
        except Exception as e:
            logger.warning(f"Could not scrape full article body from {url}: {e}")
        return ""

    def process_with_gemini(self, title: str, raw_content: str, source_url: str) -> Optional[Dict[str, Any]]:
        """Passes news item to Gemini model to generate structured JSON summary."""
        if not GEMINI_API_KEY:
            return self._generate_fallback_json(title, raw_content, source_url)

        model = genai.GenerativeModel("gemini-2.5-flash")
        
        prompt = f"""
Source URL: {source_url}
Original Title: {title}
Raw Content:
{raw_content[:2500]}
"""

        for attempt in range(3):
            try:
                response = model.generate_content(
                    prompt,
                    generation_config={
                        "response_mime_type": "application/json",
                        "temperature": 0.2
                    },
                    system_instruction=SYSTEM_PROMPT
                )
                
                text_response = response.text.strip()
                if text_response.startswith("```json"):
                    text_response = text_response[7:-3].strip()
                elif text_response.startswith("```"):
                    text_response = text_response[3:-3].strip()

                data = json.loads(text_response)
                data["source_url"] = source_url
                
                summary_bullets = data.get("summary", [])
                full_summary_text = " ".join(summary_bullets)
                words = full_summary_text.split()
                if len(words) > 60:
                    data["summary_text"] = " ".join(words[:60])
                else:
                    data["summary_text"] = full_summary_text

                return data

            except Exception as e:
                logger.error(f"Gemini API processing error (attempt {attempt+1}): {e}")
                time.sleep(2 * (attempt + 1))

        return self._generate_fallback_json(title, raw_content, source_url)

    def _generate_fallback_json(self, title: str, content: str, source_url: str) -> Dict[str, Any]:
        """Generates fallback structured JSON if Gemini API is unreachable or rate limited."""
        clean_text = self.clean_html(content)[:200]
        category = "ITR & Tax"
        title_lower = title.lower()
        if "credit card" in title_lower or "bank" in title_lower:
            category = "Credit Cards"
        elif "fd" in title_lower or "loan" in title_lower or "interest" in title_lower:
            category = "Loans & FDs"
        elif "market" in title_lower or "mutual fund" in title_lower or "nifty" in title_lower or "sip" in title_lower:
            category = "Markets & Mutual Funds"
        elif "rbi" in title_lower or "policy" in title_lower or "repo rate" in title_lower:
            category = "RBI & Policy"

        bullets = [
            f"What happened: {title[:80]}.",
            "Who is impacted: Indian taxpayers, salaried individuals, and retail investors.",
            "Actionable Takeaway: Check official portal for guidelines and verify impact on your portfolio."
        ]
        
        summary_text = " ".join(bullets)
        words = summary_text.split()
        if len(words) > 60:
            summary_text = " ".join(words[:60])

        return {
            "title": ' '.join(title.split()[:10]),
            "summary": bullets,
            "summary_text": summary_text,
            "category": category,
            "financial_action_url": "https://eportal.incometax.gov.in" if category == "ITR & Tax" else None,
            "source_url": source_url
        }

    def generate_audio_gtts(self, summary_text: str, article_hash: str) -> str:
        """Converts text (<60 words) to speech MP3 file using gTTS."""
        filename = f"news_{article_hash}.mp3"
        filepath = os.path.join(AUDIO_OUTPUT_DIR, filename)

        try:
            logger.info(f"Generating TTS audio for: {summary_text[:40]}...")
            tts = gTTS(text=summary_text, lang="en", tld="co.in", slow=False)
            tts.save(filepath)
            logger.info(f"Audio file saved locally: {filepath}")

            if self.supabase:
                try:
                    with open(filepath, "rb") as f:
                        file_bytes = f.read()
                    self.supabase.storage.from_(AUDIO_STORAGE_BUCKET).upload(
                        path=filename,
                        file=file_bytes,
                        file_options={"content-type": "audio/mpeg", "x-upsert": "true"}
                    )
                    public_url = self.supabase.storage.from_(AUDIO_STORAGE_BUCKET).get_public_url(filename)
                    logger.info(f"Audio uploaded to Supabase Storage: {public_url}")
                    return public_url
                except Exception as storage_err:
                    logger.warning(f"Supabase storage upload error: {storage_err}")

            return f"file://{os.path.abspath(filepath)}"

        except Exception as e:
            logger.error(f"Failed to generate gTTS audio: {e}")
            return ""

    def save_to_supabase(self, record: Dict[str, Any]) -> bool:
        """Persists structured JSON and audio link into Supabase 'financial_news' table."""
        if not self.supabase:
            logger.info(f"[SIMULATED SAVE] {json.dumps(record, indent=2)}")
            return True

        try:
            db_payload = {
                "title": record["title"],
                "summary": record["summary"],
                "summary_text": record["summary_text"],
                "category": record["category"],
                "financial_action_url": record.get("financial_action_url"),
                "source_url": record["source_url"],
                "audio_url": record.get("audio_url"),
                "source_name": record.get("source_name", "Indian Financial News")
            }

            response = self.supabase.table("financial_news").upsert(
                db_payload,
                on_conflict="source_url"
            ).execute()

            logger.info(f"Successfully saved item to Supabase table 'financial_news'. Source: {record['source_url']}")
            return True

        except Exception as e:
            logger.error(f"Supabase database error saving {record['source_url']}: {e}")
            return False

    def run_pipeline(self, max_items_per_feed: int = 15):
        """Executes the full web scraping -> NLP summary -> TTS -> JSON pipeline."""
        logger.info("Starting FinTax Financial News Processing Pipeline...")
        total_processed = 0
        
        # Load existing json if it exists
        live_news = []
        if os.path.exists("live_news.json"):
            try:
                with open("live_news.json", "r") as f:
                    live_news = json.load(f)
            except Exception as e:
                logger.error(f"Error loading live_news.json: {e}")

        # Keep a set of existing URLs to avoid duplicates
        existing_urls = {item.get("source_url") for item in live_news}
        
        new_articles = []

        for feed in INDIAN_FINANCIAL_FEEDS:
            logger.info(f"Fetching RSS feed: {feed['name']} ({feed['url']})")
            try:
                parsed_feed = feedparser.parse(feed['url'])
                entries = parsed_feed.entries[:max_items_per_feed]

                for entry in entries:
                    source_url = getattr(entry, "link", "")
                    
                    if not source_url or source_url in existing_urls:
                        continue
                        
                    raw_title = getattr(entry, "title", "Indian Financial News Update")
                    raw_desc = getattr(entry, "description", getattr(entry, "summary", ""))
                    clean_desc = self.clean_html(raw_desc)

                    logger.info(f"Processing news article: {raw_title}")

                    body_text = self.scrape_article_body(source_url)
                    combined_content = f"{clean_desc} {body_text}".strip()

                    news_data = self.process_with_gemini(raw_title, combined_content, source_url)
                    if not news_data:
                        continue

                    news_data["source_name"] = feed["name"]

                    url_hash = hashlib.md5(source_url.encode("utf-8")).hexdigest()[:10]
                    audio_link = self.generate_audio_gtts(news_data["summary_text"], url_hash)
                    news_data["audio_url"] = audio_link

                    self.save_to_supabase(news_data)
                    
                    new_articles.append(news_data)

                    total_processed += 1
                    time.sleep(1.5)

            except Exception as e:
                logger.error(f"Error processing feed {feed['name']}: {e}")

        # Prepend new articles to the top
        live_news = new_articles + live_news
        # Optional: cap at 200 articles to prevent infinite growth
        live_news = live_news[:200]

        with open("live_news.json", "w") as f:
            json.dump(live_news, f, indent=2)
            
        logger.info(f"Pipeline Execution Complete! Total new items processed: {total_processed}")

if __name__ == "__main__":
    pipeline = FinancialNewsScraperPipeline()
    pipeline.run_pipeline(max_items_per_feed=2)
