import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

old_block = """    try:
        req = urllib.request.Request(
            url,
            data=json.dumps(payload).encode('utf-8'),
            headers={'Content-Type': 'application/json'},
            method='POST'
        )
        with urllib.request.urlopen(req, timeout=25) as resp:
            data = json.loads(resp.read().decode('utf-8'))
            text_resp = data["candidates"][0]["content"]["parts"][0]["text"].strip()
            
            if text_resp.startswith("```json"): text_resp = text_resp[7:]
            if text_resp.startswith("```"): text_resp = text_resp[3:]
            if text_resp.endswith("```"): text_resp = text_resp[:-3]
            
            parsed_list = json.loads(text_resp.strip())
            return {int(obj["id"]): obj for obj in parsed_list if "id" in obj}
    except Exception as e:
        logging.error(f"Gemini API call failed: {e}")
        return {}"""

new_block = """    max_retries = 4
    for attempt in range(max_retries):
        try:
            req = urllib.request.Request(
                url,
                data=json.dumps(payload).encode('utf-8'),
                headers={'Content-Type': 'application/json'},
                method='POST'
            )
            with urllib.request.urlopen(req, timeout=25) as resp:
                data = json.loads(resp.read().decode('utf-8'))
                text_resp = data["candidates"][0]["content"]["parts"][0]["text"].strip()
                
                if text_resp.startswith("```json"): text_resp = text_resp[7:]
                if text_resp.startswith("```"): text_resp = text_resp[3:]
                if text_resp.endswith("```"): text_resp = text_resp[:-3]
                
                parsed_list = json.loads(text_resp.strip())
                return {int(obj["id"]): obj for obj in parsed_list if "id" in obj}
        except urllib.error.HTTPError as e:
            if e.code == 429:
                delay = 2 ** attempt * 5  # 5s, 10s, 20s, 40s
                logging.warning(f"Rate limited (429). Retrying in {delay} seconds...")
                time.sleep(delay)
            else:
                logging.error(f"Gemini API HTTP Error: {e}")
                break
        except Exception as e:
            logging.error(f"Gemini API call failed: {e}")
            break
            
    return {}"""

if old_block in content:
    content = content.replace(old_block, new_block)
    with open("backend_pipeline/news_scraper_cron.py", "w") as f:
        f.write(content)
    print("Patch applied successfully.")
else:
    print("Old block not found!")
