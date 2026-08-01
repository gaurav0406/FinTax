import os
import urllib.request

SUPABASE_URL = os.getenv("SUPABASE_URL", "")
SUPABASE_KEY = os.getenv("SUPABASE_SERVICE_ROLE_KEY", os.getenv("SUPABASE_KEY", ""))

if not SUPABASE_URL or not SUPABASE_KEY:
    print("No Supabase credentials.")
    exit(1)

url = f"{SUPABASE_URL.rstrip('/')}/rest/v1/financial_news?id=gt.-1"
headers = {
    'apikey': SUPABASE_KEY,
    'Authorization': f'Bearer {SUPABASE_KEY}',
    'Content-Type': 'application/json',
}

req = urllib.request.Request(url, headers=headers, method='DELETE')
try:
    with urllib.request.urlopen(req) as res:
        print(f"Delete response: {res.status}")
except Exception as e:
    print(f"Error: {e}")
