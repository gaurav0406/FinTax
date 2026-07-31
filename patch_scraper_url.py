import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

content = content.replace('"financialActionUrl": "https://example.com",', '"financialActionUrl": r["url"],')

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
