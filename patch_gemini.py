import re

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

new_content = content.replace(
    '"summary": "Provide a cohesive, high-readability 6 to 7 line narrative overview covering what happened and the core market context.",',
    '"summary": "Provide a cohesive, high-readability 5 to 6 line narrative overview covering what happened and the core market context.",'
)

with open("backend_pipeline/news_scraper_cron.py", "w") as f:
    f.write(new_content)
