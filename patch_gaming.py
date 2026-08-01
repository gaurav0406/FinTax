import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

# Add Gaming feed
if '"category": "Gaming"' not in content:
    content = content.replace(
        '        "sourceName": "Entertainment Week"\n    },',
        '        "sourceName": "Entertainment Week"\n    },\n    {\n        "category": "Gaming",\n        "url": "https://www.gamespot.com/feeds/news/",\n        "sourceName": "GameSpot"\n    },'
    )
    # Just insert it into the feeds list
    content = content.replace(
        '        "sourceName": "Bollywood Hungama"\n    },',
        '        "sourceName": "Bollywood Hungama"\n    },\n    {\n        "category": "Gaming",\n        "url": "https://www.gamespot.com/feeds/news/",\n        "sourceName": "GameSpot"\n    },'
    )

content = content.replace("Entertainment', 'ITR & Tax'", "Entertainment', 'Gaming', 'ITR & Tax'")
content = content.replace('["Credit Cards", "Mutual Funds", "Crypto", "Cars & EVs", "Technology", "Sports", "Education", "Entertainment", "ITR & Tax", "Loans & FDs", "Markets & Mutual Funds", "RBI & Policy"]', '["Credit Cards", "Mutual Funds", "Crypto", "Cars & EVs", "Technology", "Sports", "Education", "Entertainment", "Gaming", "ITR & Tax", "Loans & FDs", "Markets & Mutual Funds", "RBI & Policy"]')

gaming_logic = """    # 7. Gaming
    if any(k in content for k in [
        "xbox", "playstation", "nintendo", "pc gaming", "steam", "esports", "twitch", "gameplay", "ps5"
    ]):
        return "Gaming"
        
"""

if "# 7. Gaming" not in content:
    content = content.replace("    # Default to feed category", gaming_logic + "    # Default to feed category")

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(content)
