import re

# 1. Add stripIntroductoryLabels to FinancialNewsEntity.kt
with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    entity = f.read()

strip_func = '''
fun String.stripIntroductoryLabels(): String {
    if (this.isBlank()) return this
    return this
        .replace(Regex("(?m)(^|\\\\n)(•\\\\s*)?(Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update|Key Highlight|Practical Takeaway|Direct Cash Impact|Net Card Yield|Interest Yield|Loan EMI Impact|Operational Savings|Tax Incentive|Liquidity Boost|Expected Yield|Financial Gain|Championship Standing|Curriculum Shift|Streaming Rights|Infrastructure Boost|Tech Efficiency|Workflow Automation|Career Advantage|Ecosystem Growth|Job Creation|Reason for change|Audience Value|Skill Demand|Quantifiable Benefit):\\\\s*", RegexOption.IGNORE_CASE), "$1$2")
        .replace(Regex("(?i)\\\\b(Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update):\\\\s*"), "")
        .trim()
}
'''

if "fun String.stripIntroductoryLabels()" not in entity:
    entity += strip_func
    with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
        f.write(entity)
    print("Added stripIntroductoryLabels to FinancialNewsEntity.kt")

# 2. Remove duplicate definitions from InshortsFeedView.kt and NewsItemCard.kt
for file_path in ["app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "app/src/main/java/com/example/ui/components/NewsItemCard.kt"]:
    with open(file_path, "r") as f:
        content = f.read()
    
    # Remove top level fun String.stripIntroductoryLabels()
    content = re.sub(r'fun String\.stripIntroductoryLabels\(\): String \{.*?\n\}', '', content, flags=re.DOTALL)
    
    with open(file_path, "w") as f:
        f.write(content)
    print(f"Cleaned duplicate extension from {file_path}")

# 3. Fix commentCount parameter in VideoEngagementTab.kt
with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "r") as f:
    video_tab = f.read()

video_tab = video_tab.replace(",\n                    commentCount = 180 + i * 24", "")
video_tab = video_tab.replace(", commentCount = 180 + i * 24", "")

# Remove duplicate stripIntroductoryLabels if present
video_tab = re.sub(r'fun String\.stripIntroductoryLabels\(\): String \{.*?\n\}', '', video_tab, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/components/VideoEngagementTab.kt", "w") as f:
    f.write(video_tab)
print("Fixed VideoEngagementTab.kt")

