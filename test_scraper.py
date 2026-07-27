import logging
import feedparser
from bs4 import BeautifulSoup

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def fetch_financial_news():
    logging.info("Scraping financial news...")
    
    feeds = [
        {"category": "Stock Market India", "url": "https://economictimes.indiatimes.com/markets/rssfeeds/2146842.cms"},
        {"category": "Credit Cards", "url": "https://economictimes.indiatimes.com/wealth/borrow/rssfeeds/83756073.cms"},
        {"category": "ITR & Tax", "url": "https://economictimes.indiatimes.com/wealth/tax/rssfeeds/83755913.cms"},
        {"category": "Markets & Mutual Funds", "url": "https://economictimes.indiatimes.com/mf/rssfeeds/83756208.cms"}
    ]
    
    articles = []
    for feed in feeds:
        try:
            parsed = feedparser.parse(feed["url"])
            count = len(parsed.entries[:15])
            logging.info(f"Fetched {count} articles for {feed['category']}")
            for entry in parsed.entries[:2]: # Just printing first 2 to verify format
                summary_html = entry.get("summary", "")
                soup = BeautifulSoup(summary_html, 'html.parser')
                clean_text = soup.get_text(strip=True)
                if not clean_text:
                    clean_text = entry.get("title", "")
                print(f"[{feed['category']}] {entry.get('title', '')}")
            
            # append actual count to test list to verify it's > 10
            articles.append({"category": feed["category"], "count": count})
        except Exception as e:
            logging.error(f"Error fetching RSS feed {feed['url']}: {e}")
            
    return articles

articles_summary = fetch_financial_news()
for s in articles_summary:
    print(f"Summary -> Category: {s['category']} | Count: {s['count']}")
