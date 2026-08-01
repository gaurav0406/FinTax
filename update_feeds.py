import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

new_feeds = """FEEDS = [
    # Credit Card Sources
    {
        "category": "Credit Cards",
        "url": "https://thepointsguy.com/feed/",
        "sourceName": "The Points Guy"
    },
    {
        "category": "Credit Cards",
        "url": "https://www.doctorofcredit.com/feed/",
        "sourceName": "Doctor Of Credit"
    },
    {
        "category": "Credit Cards",
        "url": "https://viewfromthewing.com/feed/",
        "sourceName": "View from the Wing"
    },
    {
        "category": "Credit Cards",
        "url": "https://onemileatatime.com/feed/",
        "sourceName": "One Mile at a Time"
    },
    {
        "category": "Credit Cards",
        "url": "https://cardinsider.com/feed/",
        "sourceName": "Card Insider"
    },

    # Financial News Sources
    {
        "category": "Financial News",
        "url": "https://www.moneycontrol.com/rss/MCtopnews.xml",
        "sourceName": "Moneycontrol Top News"
    },
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
        "url": "https://search.cnbc.com/rs/search/combinedcms/view.xml?profile=120000000&id=10000664",
        "sourceName": "CNBC Finance"
    },
    {
        "category": "Financial News",
        "url": "https://finance.yahoo.com/news/rss",
        "sourceName": "Yahoo Finance"
    },
    {
        "category": "Financial News",
        "url": "https://feeds.a.dj.com/rss/WSJcomUSBusiness.xml",
        "sourceName": "WSJ Business"
    },
    {
        "category": "Financial News",
        "url": "https://www.investing.com/rss/news_25.rss",
        "sourceName": "Investing.com"
    },
    {
        "category": "Financial News",
        "url": "https://www.livemint.com/rss/markets",
        "sourceName": "LiveMint Markets"
    },
    {
        "category": "Markets & Mutual Funds",
        "url": "https://www.moneycontrol.com/rss/mfnews.xml",
        "sourceName": "Moneycontrol Mutual Funds"
    },
    {
        "category": "Markets & Mutual Funds",
        "url": "https://www.livemint.com/rss/mutual-funds",
        "sourceName": "LiveMint Mutual Funds"
    }
]"""

# Find the FEEDS list block and replace it
content = re.sub(r'FEEDS = \[.*?\](?=\n\ndef )', new_feeds, content, flags=re.DOTALL)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
