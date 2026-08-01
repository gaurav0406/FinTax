import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

# Replace the push_to_supabase_rest one
content = content.replace(
    '"imageUrl": item.get("imageUrl") or "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",',
    '"imageUrl": r.get("imageUrl") if \'r\' in locals() else item.get("imageUrl", "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60"),'
)

# Better yet, just use a regex
content = re.sub(
    r'"imageUrl": item.get\("imageUrl"\) or "(https://[^"]+)",',
    r'"imageUrl": r.get("imageUrl") if \'r\' in locals() else item.get("imageUrl") or "\1",',
    content
)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
