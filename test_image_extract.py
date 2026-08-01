import re
import urllib.request

url = "https://thepointsguy.com/feed/"
headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
req = urllib.request.Request(url, headers=headers)
with urllib.request.urlopen(req, timeout=8) as response:
    xml_data = response.read().decode('utf-8', errors='ignore')
    xml_clean = re.sub(r'[\x00-\x08\x0B\x0C\x0E-\x1F]', '', xml_data)
    
item_blocks = re.findall(r'<item>(.*?)</item>', xml_clean, re.DOTALL | re.IGNORECASE)
for block in item_blocks[:2]:
    cb = block.replace('<![CDATA[', '').replace(']]>', '')
    
    img_m = re.search(r'<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\'"]([^\'"]+)[\'"]', cb, re.IGNORECASE)
    image_url = img_m.group(1) if img_m else None
    
    desc_m = re.search(r'<(?:description|summary)>(.*?)</(?:description|summary)>', cb, re.DOTALL | re.IGNORECASE)
    if not image_url and desc_m:
        img_desc = re.search(r'<img[^>]*src=[\'"]([^\'"]+)[\'"]', desc_m.group(1), re.IGNORECASE)
        if img_desc:
            image_url = img_desc.group(1)
            
    print(f"Extracted image URL: {image_url}")
