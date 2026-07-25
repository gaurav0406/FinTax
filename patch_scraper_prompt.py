import re

with open("financial_news_scraper.py", "r") as f:
    content = f.read()

old_prompt = """SYSTEM_PROMPT = \"\"\"
You are an expert Indian Financial NLP & Tax Journalist.
Your task is to analyze raw news articles and produce structured, actionable intelligence specifically for Indian taxpayers and retail investors.

Output MUST be strictly valid JSON without any markdown codeblock formatting.

Required JSON Structure:
{
  "title": "Catchy headline tailored for Indian taxpayers/investors (Max 10 words)",
  "summary": [
    "Point 1: What happened (1-2 sentences)",
    "Point 2: Who is impacted e.g. Salaried Class, Senior Citizens, Taxpayers (1 sentence)",
    "Point 3: Actionable Takeaway e.g. File ITR-1 before July 31, Link Aadhaar (1 sentence)"
  ],
  "category": "Must be EXACTLY ONE of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy']",
  "financial_action_url": "Optional affiliate or official portal URL (e.g. incometax.gov.in, rbi.org.in, or null)",
  "source_url": "Original article link"
}

Constraints:
1. 'title' must be concise and engaging (Maximum 10 words).
2. 'summary' must contain EXACTLY 3 bullet points.
3. The total word count of all 3 bullet points combined MUST NOT exceed 60 words.
4. 'category' must strictly match one of the 5 allowed values.
5. Content must focus on practical financial implications for Indians (Section 80C, Income Tax Slabs, Repo Rate, FD Rates, Credit Card Rewards, Mutual Fund NAV).
\"\"\""""

new_prompt = """SYSTEM_PROMPT = \"\"\"
You are an expert Analyst and Journalist.
Your task is to analyze raw news articles and produce structured, actionable intelligence.

Output MUST be strictly valid JSON without any markdown codeblock formatting.

Required JSON Structure:
{
  "title": "Catchy headline tailored for readers (Max 10 words)",
  "summary": [
    "Point 1: What happened (2-3 sentences providing a detailed overview)",
    "Point 2: Who is impacted e.g. Salaried Class, Tech workers, specific demographics (2 sentences)",
    "Point 3: Actionable Takeaway e.g. Steps to mitigate risk, investments, tech adoption (2 sentences)"
  ],
  "category": "Must be EXACTLY ONE of ['Credit Cards', 'ITR & Tax', 'Loans & FDs', 'Markets & Mutual Funds', 'RBI & Policy', 'Entertainment', 'Technology Insights', 'AI & New Happenings', 'Personal Finance', 'Global Markets', 'Startup Ecosystem', 'Real Estate & Mortgages', 'Cryptocurrency & Web3']",
  "financial_action_url": "Optional affiliate or official portal URL (e.g. incometax.gov.in, rbi.org.in, or null)",
  "source_url": "Original article link"
}

Constraints:
1. 'title' must be concise and engaging (Maximum 10 words).
2. 'summary' must contain EXACTLY 3 bullet points, each roughly two sentences long.
3. The total word count of all 3 bullet points combined should be around 100-150 words.
4. 'category' must strictly match one of the allowed values.
5. Content must focus on practical implications for the reader.
\"\"\""""

content = content.replace(old_prompt, new_prompt)

# Also update word count check
content = content.replace("if len(words) > 60:", "if len(words) > 180:")
content = content.replace('data["summary_text"] = " ".join(words[:60])', 'data["summary_text"] = " ".join(words[:180])')

with open("financial_news_scraper.py", "w") as f:
    f.write(content)
