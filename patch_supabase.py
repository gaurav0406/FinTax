import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

old_block = """    try:
        req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method='POST')
        with urllib.request.urlopen(req, timeout=10) as resp:
            logging.info(f"Supabase REST push SUCCESS! Status: {resp.status}")
    except Exception as e:
        logging.error(f"Supabase REST upload failed: {e}")"""

new_block = """    try:
        req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method='POST')
        with urllib.request.urlopen(req, timeout=10) as resp:
            logging.info(f"Supabase REST push SUCCESS! Status: {resp.status}")
    except urllib.error.HTTPError as e:
        error_body = e.read().decode('utf-8')
        logging.error(f"Supabase REST upload failed: HTTP Error {e.code}: {e.reason} - {error_body}")
    except Exception as e:
        logging.error(f"Supabase REST upload failed: {e}")"""

if old_block in content:
    content = content.replace(old_block, new_block)
    with open("backend_pipeline/news_scraper_cron.py", "w") as f:
        f.write(content)
    print("Patch applied successfully.")
else:
    print("Old block not found!")
