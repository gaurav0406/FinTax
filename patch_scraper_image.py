import re

def patch_file(filepath):
    with open(filepath, "r") as f:
        content = f.read()
    
    # Update fetch_rss_feed_fast to extract imageUrl
    old_func = """            title = clean_html_text(title_m.group(1)) if title_m else ""
            link = clean_html_text(link_m.group(1)) if link_m else ""
            desc = clean_html_text(desc_m.group(1)) if desc_m else title
            if title and link:
                clean_link = link.split("?")[0]
                items.append({
                    "title": title[:250],
                    "url": clean_link,
                    "text": desc[:1000] if len(desc) > 10 else title,
                    "category": feed_info["category"],
                    "sourceName": feed_info["sourceName"]
                })"""

    new_func = """            title = clean_html_text(title_m.group(1)) if title_m else ""
            link = clean_html_text(link_m.group(1)) if link_m else ""
            desc = clean_html_text(desc_m.group(1)) if desc_m else title
            
            # Extract original image URL
            img_url = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60"
            encl_m = re.search(r'<enclosure[^>]+url=["\']([^"\']+)["\']', cb, re.IGNORECASE)
            media_m = re.search(r'<media:content[^>]+url=["\']([^"\']+)["\']', cb, re.IGNORECASE)
            img_m = re.search(r'<img[^>]+src=["\']([^"\']+)["\']', cb, re.IGNORECASE)
            if encl_m and any(ext in encl_m.group(1).lower() for ext in ['.jpg', '.jpeg', '.png', '.webp']):
                img_url = encl_m.group(1)
            elif media_m and any(ext in media_m.group(1).lower() for ext in ['.jpg', '.jpeg', '.png', '.webp']):
                img_url = media_m.group(1)
            elif img_m and any(ext in img_m.group(1).lower() for ext in ['.jpg', '.jpeg', '.png', '.webp']):
                img_url = img_m.group(1)

            if title and link:
                clean_link = link.split("?")[0]
                items.append({
                    "title": title[:250],
                    "url": clean_link,
                    "text": desc[:1000] if len(desc) > 10 else title,
                    "category": feed_info["category"],
                    "sourceName": feed_info["sourceName"],
                    "imageUrl": img_url
                })"""

    content = content.replace(old_func, new_func)

    # Also update where processed_item assigns imageUrl from item if available
    content = content.replace(
        '"imageUrl": "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60",',
        '"imageUrl": item.get("imageUrl", "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800&auto=format&fit=crop&q=60"),'
    )

    with open(filepath, "w") as f:
        f.write(content)

patch_file("backend/financial_news_scraper.py")
patch_file("backend_pipeline/news_scraper_cron.py")
print("Successfully patched scraper scripts.")
