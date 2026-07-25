with open("financial_news_scraper.py", "r") as f:
    content = f.read()

old_feeds = """INDIAN_FINANCIAL_FEEDS = [
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
]"""

new_feeds = """INDIAN_FINANCIAL_FEEDS = [
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
    },
    {
        "name": "TechCrunch AI",
        "url": "https://techcrunch.com/category/artificial-intelligence/feed/",
        "default_category": "AI & New Happenings"
    },
    {
        "name": "Gadgets 360",
        "url": "https://www.gadgets360.com/rss/feeds",
        "default_category": "Technology Insights"
    },
    {
        "name": "TOI Entertainment",
        "url": "https://timesofindia.indiatimes.com/rssfeeds/1081479906.cms",
        "default_category": "Entertainment"
    },
    {
        "name": "CoinDesk",
        "url": "https://www.coindesk.com/arc/outboundfeeds/rss/",
        "default_category": "Cryptocurrency & Web3"
    },
    {
        "name": "ET Startups",
        "url": "https://economictimes.indiatimes.com/tech/startups/rssfeeds/76432395.cms",
        "default_category": "Startup Ecosystem"
    }
]"""

content = content.replace(old_feeds, new_feeds)

with open("financial_news_scraper.py", "w") as f:
    f.write(content)
