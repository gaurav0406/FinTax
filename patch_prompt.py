import sys

with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

target = """"reason": "Provide 3-4 crisp bullet points explaining the core reasons or causes. Do NOT use introductory labels.","""

replacement = """"reason": "Write a 3-4 sentence engaging, conversational TV news-anchor script explaining why this news matters and the core reasons behind it. Act like a live news anchor speaking to the audience (e.g., 'Here is why this matters to you...', 'The driving force behind this update is...'). Do NOT use bullet points. Ensure it reads smoothly for Text-to-Speech audio.","""

if target in content:
    content = content.replace(target, replacement)
    with open("backend_pipeline/news_scraper_cron.py", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Not found")

