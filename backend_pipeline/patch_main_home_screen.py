import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

categories_new = """val CATEGORIES = listOf(
    "All",
    "Card Hacks & Perks",
    "Financial Markets",
    "Tech & AI",
    "Startup & Capital",
    "Wealth 101"
)"""
content = re.sub(r'val CATEGORIES = listOf\([\s\S]*?\)[\s]*\n', categories_new + "\n\n", content, count=1)

filter_logic_old = """                (selectedCategory == "Credit Cards" && (news.category.contains("Card", ignoreCase = true) || news.title.contains("Card", ignoreCase = true))) ||
                (selectedCategory == "ITR & Tax" && (news.category.contains("Tax", ignoreCase = true) || news.category.contains("ITR", ignoreCase = true))) ||
                (selectedCategory == "Technology" && (news.category.contains("Tech", ignoreCase = true) || news.category.contains("AI", ignoreCase = true))) ||
                (selectedCategory == "Markets & Mutual Funds" && (news.category.contains("Market", ignoreCase = true) || news.category.contains("Mutual", ignoreCase = true) || news.category.contains("Fund", ignoreCase = true) || news.category.contains("Stock", ignoreCase = true))) ||
                (selectedCategory == "Loans & FDs" && (news.category.contains("Loan", ignoreCase = true) || news.category.contains("FD", ignoreCase = true))) ||
                (selectedCategory == "RBI & Policy" && (news.category.contains("RBI", ignoreCase = true) || news.category.contains("Policy", ignoreCase = true))) ||
                (selectedCategory == "Financial News" && (news.category.contains("Financial", ignoreCase = true) || news.category.contains("News", ignoreCase = true)))"""
                
filter_logic_new = """                (selectedCategory == "Card Hacks & Perks" && (news.category.contains("Card", ignoreCase = true) || news.title.contains("Card", ignoreCase = true))) ||
                (selectedCategory == "Financial Markets" && (news.category.contains("Market", ignoreCase = true) || news.category.contains("Mutual", ignoreCase = true) || news.category.contains("Stock", ignoreCase = true))) ||
                (selectedCategory == "Tech & AI" && (news.category.contains("Tech", ignoreCase = true) || news.category.contains("AI", ignoreCase = true))) ||
                (selectedCategory == "Startup & Capital" && (news.category.contains("Startup", ignoreCase = true) || news.category.contains("Capital", ignoreCase = true))) ||
                (selectedCategory == "Wealth 101" && (news.category.contains("Wealth", ignoreCase = true) || news.category.contains("Finance", ignoreCase = true)))"""

content = content.replace(filter_logic_old, filter_logic_new)

# Subtitle change
content = content.replace('1 -> "Discuss Tax, Tech, Stocks & Crypto"', '1 -> "Discuss Wealth, Tech, Startups & Perks"')

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
