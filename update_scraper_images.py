import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

# Replace RSS fetching logic
rss_old = """            if pubDate_m:
                import email.utils
                try:
                    parsed_tuple = email.utils.parsedate_tz(pubDate_m.group(1))
                    if parsed_tuple:
                        pub_time = email.utils.mktime_tz(parsed_tuple)
                except:
                    pass
            
            if title and link:
                clean_link = link.split("?")[0]
                items.append({
                    "title": title[:250],
                    "url": clean_link,
                    "text": desc[:1000] if len(desc) > 10 else title,
                    "category": feed_info["category"],
                    "sourceName": feed_info["sourceName"],
                    "publishedAt": int(pub_time * 1000) if pub_time else None
                })"""

rss_new = """            if pubDate_m:
                import email.utils
                try:
                    parsed_tuple = email.utils.parsedate_tz(pubDate_m.group(1))
                    if parsed_tuple:
                        pub_time = email.utils.mktime_tz(parsed_tuple)
                except:
                    pass
            
            # Extract image
            img_m = re.search(r'<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\'"]([^\'"]+)[\'"]', cb, re.IGNORECASE)
            image_url = img_m.group(1) if img_m else None
            
            # Try parsing from description if not found
            if not image_url and desc_m:
                img_desc = re.search(r'<img[^>]*src=[\'"]([^\'"]+)[\'"]', desc_m.group(1), re.IGNORECASE)
                if img_desc:
                    image_url = img_desc.group(1)
            
            if title and link:
                clean_link = link.split("?")[0]
                items.append({
                    "title": title[:250],
                    "url": clean_link,
                    "text": desc[:1000] if len(desc) > 10 else title,
                    "category": feed_info["category"],
                    "sourceName": feed_info["sourceName"],
                    "imageUrl": image_url,
                    "publishedAt": int(pub_time * 1000) if pub_time else None
                })"""

content = content.replace(rss_old, rss_new)

# In the format output, use the item['imageUrl'] if available, else a generated one by Gemini or a fallback
format_old = """            "imageUrl": "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60","""
format_new = """            "imageUrl": item.get("imageUrl") or "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60","""
content = content.replace(format_old, format_new)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
