import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

# Replace the unsplash fallback
content = re.sub(
    r'"imageUrl": r\.get\("imageUrl"\) or "https://images\.unsplash\.com[^"]+",',
    r'"imageUrl": r.get("imageUrl"),',
    content
)

content = re.sub(
    r'"imageUrl": item\.get\("imageUrl"\) or "https://images\.unsplash\.com[^"]+",',
    r'"imageUrl": item.get("imageUrl"),',
    content
)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
