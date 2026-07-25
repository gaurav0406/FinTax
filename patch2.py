import re
with open("financial_news_scraper.py", "r") as f:
    content = f.read()

# Fix the indentation of run_pipeline
def fix_line(line):
    if line.startswith("def run_pipeline"):
        return "    " + line
    # If the line starts with 4 spaces but it's supposed to be inside a class
    # The previous code:
    # def run_pipeline(self...
    #         """Executes...
    # wait, my patched block:
    return line

lines = content.split('\n')
new_lines = []
for line in lines:
    if line.startswith("def run_pipeline(self, max_items_per_feed"):
        new_lines.append("    " + line)
    else:
        new_lines.append(line)

with open("financial_news_scraper.py", "w") as f:
    f.write('\n'.join(new_lines))
