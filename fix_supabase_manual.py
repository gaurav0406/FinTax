import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

content = content.replace(
    '"imageUrl": r.get("imageUrl") if \'r\' in locals() else item.get("imageUrl", "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60"),',
    '"imageUrl": r.get("imageUrl") or "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",'
)

content = content.replace(
    '"imageUrl": r.get("imageUrl") if \'r\' in locals() else item.get("imageUrl") or "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",',
    '"imageUrl": locals().get("r", locals().get("item", {})).get("imageUrl") or "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",'
)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
