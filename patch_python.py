import re
with open("financial_news_scraper.py", "r") as f:
    content = f.read()

# Add saving to live_news.json
run_pipeline_code = """
    def run_pipeline(self, max_items_per_feed: int = 15):
        \"\"\"Executes the full web scraping -> NLP summary -> TTS -> JSON pipeline.\"\"\"
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
"""

content = re.sub(r'    def run_pipeline\(self.*?Total news items processed.*?total_processed\}"\)', run_pipeline_code.strip(), content, flags=re.DOTALL)

with open("financial_news_scraper.py", "w") as f:
    f.write(content)
