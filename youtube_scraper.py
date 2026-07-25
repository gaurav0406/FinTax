import urllib.request
import xml.etree.ElementTree as ET
import json
import os
from datetime import datetime

CHANNEL_IDS = [
    {"name": "Finance with Sharan", "id": "UCwVEhEzsjLym_u1he4XWFkg"},
    {"name": "Pranjal Kamra", "id": "UCwNxs8qGpNwAOVFlfc1QIdA"},
    {"name": "CA Rachana Ranade", "id": "UCe3qdG0A_gr-sEdat5O2aEA"},
    {"name": "Cleartax", "id": "UCz_h7s0lRXYB1a8OQcWj1xw"}
]

videos = []
for channel in CHANNEL_IDS:
    url = f"https://www.youtube.com/feeds/videos.xml?channel_id={channel['id']}"
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            xml_data = response.read()
            root = ET.fromstring(xml_data)
            
            # YouTube RSS namespaces
            ns = {'atom': 'http://www.w3.org/2005/Atom', 'yt': 'http://www.youtube.com/xml/schemas/2015'}
            
            for entry in root.findall('atom:entry', ns)[:5]: # Get latest 5 from each
                video_id = entry.find('yt:videoId', ns).text
                title = entry.find('atom:title', ns).text
                published = entry.find('atom:published', ns).text
                
                videos.append({
                    "id": video_id,
                    "title": title,
                    "channelName": channel["name"],
                    "thumbnailUrl": f"https://i.ytimg.com/vi/{video_id}/hqdefault.jpg",
                    "videoUrl": f"https://www.youtube.com/watch?v={video_id}",
                    "publishedAt": published
                })
    except Exception as e:
        print(f"Error fetching {channel['name']}: {e}")

# Sort by published date descending
videos.sort(key=lambda x: x['publishedAt'], reverse=True)

os.makedirs("app/src/main/assets", exist_ok=True)
with open("app/src/main/assets/videos.json", "w") as f:
    json.dump(videos, f, indent=4)
print(f"Successfully scraped {len(videos)} videos.")
