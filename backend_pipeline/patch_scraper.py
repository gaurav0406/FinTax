import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

# Fix Finnhub image
finnhub_old = """                    "category": "Financial News",
                    "sourceName": article.get("source", "Finnhub API")
                })"""
finnhub_new = """                    "category": "Financial News",
                    "sourceName": article.get("source", "Finnhub API"),
                    "imageUrl": article.get("image", "")
                })"""
content = content.replace(finnhub_old, finnhub_new)

# Fix RSS image extraction and link split
rss_old = """            # Extract image
            img_m = re.search(r"<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\'\"]([^\'\"]+)[\'\"]", cb, re.IGNORECASE)
            image_url = img_m.group(1) if img_m else None
            
            # Try parsing from description if not found
            if not image_url and desc_m:
                img_desc = re.search(r"<img[^>]*src=[\'\"]([^\'\"]+)[\'\"]", desc_m.group(1), re.IGNORECASE)
                if img_desc:
                    image_url = img_desc.group(1)
            
            if title and link:
                clean_link = link.split("?")[0]"""

rss_new = """            # Extract image
            img_m = re.search(r"<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\'\"]([^\'\"]+)[\'\"]", cb, re.IGNORECASE)
            image_url = img_m.group(1) if img_m else None
            
            # Additional check for image tag directly inside item content
            if not image_url:
                img_tag = re.search(r"<image>.*?<url>(.*?)</url>.*?</image>", cb, re.IGNORECASE | re.DOTALL)
                if img_tag:
                    image_url = img_tag.group(1)

            # Try parsing from description if not found
            if not image_url and desc_m:
                img_desc = re.search(r"<img[^>]*src=[\'\"]([^\'\"]+)[\'\"]", desc_m.group(1), re.IGNORECASE)
                if img_desc:
                    image_url = img_desc.group(1)
                    
            # Check content:encoded for images
            if not image_url:
                content_encoded_m = re.search(r'<content:encoded.*?>(.*?)</content:encoded>', cb, re.DOTALL | re.IGNORECASE)
                if content_encoded_m:
                    img_encoded = re.search(r"<img[^>]*src=[\'\"]([^\'\"]+)[\'\"]", content_encoded_m.group(1), re.IGNORECASE)
                    if img_encoded:
                        image_url = img_encoded.group(1)

            if title and link:
                clean_link = link # Don't strip query params, this can break original links"""
content = content.replace(rss_old, rss_new)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
