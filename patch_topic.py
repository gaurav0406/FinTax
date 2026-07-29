import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

old_block = """            "financialImpactBullets": llm.get("financial_impact", "")[:1500],
            "publishedAt": now_ms,
            "topicCluster": llm.get("topic_cluster", "Latest Updates")[:50]
        })"""

new_block = """            "financialImpactBullets": llm.get("financial_impact", "")[:1500],
            "publishedAt": now_ms
        })"""

if old_block in content:
    content = content.replace(old_block, new_block)
    with open("backend_pipeline/news_scraper_cron.py", "w") as f:
        f.write(content)
    print("Patch applied successfully.")
else:
    print("Old block not found!")
