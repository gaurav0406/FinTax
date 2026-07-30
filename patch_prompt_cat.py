import sys

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

target = '    "category": "Must be EXACTLY ONE of [\'Financial News\', \'Credit Cards\', \'Mutual Funds\', \'Sports\', \'Cars & EVs\', \'Education\', \'Crypto\', \'Technology\']",'
replacement = '    "category": "Must be EXACTLY ONE of [\'Financial News\', \'Credit Cards\', \'Mutual Funds\', \'Sports\', \'Cars & EVs\', \'Education\', \'Crypto\', \'Technology\', \'Entertainment\', \'ITR & Tax\', \'Loans & FDs\', \'Markets & Mutual Funds\', \'RBI & Policy\']",'

if target in content:
    content = content.replace(target, replacement)
    with open("backend_pipeline/news_scraper_cron.py", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Not found")

