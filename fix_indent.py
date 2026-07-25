with open("financial_news_scraper.py", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if line.startswith("def run_pipeline(self"):
        # We need to indent this line and the following lines until the end of the file or next unindented block
        pass

