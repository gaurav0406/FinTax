import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

# Replace the broken regex lines
old_line1 = r"img_m = re.search(r'<(?:media:content|media:thumbnail|enclosure)[^>]*url=['" + '"]([^' + '"]+)[' + "']', cb, re.IGNORECASE)"
old_line1_actual = "img_m = re.search(r'<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\\'\" ]([^\\'\" ]+)[\\'\"]', cb, re.IGNORECASE)"

content = content.replace("img_m = re.search(r'<(?:media:content|media:thumbnail|enclosure)[^>]*url=['\"]([^'\"]+)['\"]', cb, re.IGNORECASE)", 
                          'img_m = re.search(r"<(?:media:content|media:thumbnail|enclosure)[^>]*url=[\\\'\\"]([^\\\'\\"]+)[\\\'\\"]", cb, re.IGNORECASE)')

content = content.replace("img_desc = re.search(r'<img[^>]*src=['\"]([^'\"]+)['\"]', desc_m.group(1), re.IGNORECASE)",
                          'img_desc = re.search(r"<img[^>]*src=[\\\'\\"]([^\\\'\\"]+)[\\\'\\"]", desc_m.group(1), re.IGNORECASE)')

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
