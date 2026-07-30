import sys, re

# Helper function string to inject
strip_labels_func = '''
fun String.stripIntroductoryLabels(): String {
    if (this.isBlank()) return this
    return this
        .replace(Regex("(?m)(^|\\\\n)(•\\\\s*)?(Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update|Key Highlight|Practical Takeaway|Direct Cash Impact|Net Card Yield|Interest Yield|Loan EMI Impact|Operational Savings|Tax Incentive|Liquidity Boost|Expected Yield|Financial Gain|Championship Standing|Curriculum Shift|Streaming Rights|Infrastructure Boost|Tech Efficiency|Workflow Automation|Career Advantage|Ecosystem Growth|Job Creation|Reason for change|Audience Value|Skill Demand|Quantifiable Benefit):\\\\s*", RegexOption.IGNORE_CASE), "$1$2")
        .replace(Regex("(?i)\\\\b(Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update):\\\\s*"), "")
        .trim()
}
'''

# 1. Patch InshortsFeedView.kt
with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    inshorts = f.read()

if "fun String.stripIntroductoryLabels()" not in inshorts:
    inshorts += strip_labels_func

# Clean fallbacks in InshortsFeedView.kt
inshorts = inshorts.replace(
    '"• Market Update: Latest developments impacting indices and policies\\n• Investor Takeaway: Adjust portfolio based on the latest macroeconomic news"',
    '"• Regulatory policy shift impacting sector valuations by ~2.5%\\n• Capital allocation adjustment advised to optimize net return"'
)
inshorts = inshorts.replace(
    '"• Direct Cash Impact: -₹350/mo on utility caps or +5% (₹400/mo) on fuel\\n• Net Card Yield: ~₹4,800/yr optimized cashback return"',
    '"• Utility fee caps adjusted by -₹350/mo or +5% fuel waiver benefit\\n• Optimized annual cashback yield estimated at ₹4,800/yr"'
)
inshorts = inshorts.replace(
    '"• Key Highlight: Major developments and strategic updates in this domain\\n• Practical Takeaway: Core insights and essential knowledge for readers"',
    '"• Core operational developments affecting market performance\\n• Strategic findings for long-term planning"'
)

# Strip labels on content parameters in InshortsFeedView.kt
inshorts = inshorts.replace("content = news.summaryWhatHappened", "content = news.summaryWhatHappened.stripIntroductoryLabels()")
inshorts = inshorts.replace("content = news.summaryText", "content = news.summaryText.stripIntroductoryLabels()")
inshorts = inshorts.replace("content = calculatedImpact", "content = calculatedImpact.stripIntroductoryLabels()")
inshorts = inshorts.replace("content = news.summaryActionableTakeaway", "content = news.summaryActionableTakeaway.stripIntroductoryLabels()")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(inshorts)
print("Patched InshortsFeedView.kt")

# 2. Patch NewsItemCard.kt
with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    card = f.read()

if "fun String.stripIntroductoryLabels()" not in card:
    card += strip_labels_func

card = card.replace(
    '"• Market Update: Latest developments impacting indices and policies\\n• Investor Takeaway: Adjust portfolio based on the latest macroeconomic news"',
    '"• Regulatory policy shift impacting sector valuations by ~2.5%\\n• Capital allocation adjustment advised to optimize net return"'
)
card = card.replace(
    '"• Direct Cash Impact: -₹350/mo on utility caps or +5% (₹400/mo) on fuel\\n• Net Card Yield: ~₹4,800/yr optimized cashback return"',
    '"• Utility fee caps adjusted by -₹350/mo or +5% fuel waiver benefit\\n• Optimized annual cashback yield estimated at ₹4,800/yr"'
)
card = card.replace(
    '"• Key Highlight: Major developments and strategic updates in this domain\\n• Practical Takeaway: Core insights and essential knowledge for readers"',
    '"• Core operational developments affecting market performance\\n• Strategic findings for long-term planning"'
)

card = card.replace("text = news.summaryText", "text = news.summaryText.stripIntroductoryLabels()")
card = card.replace("text = cardImpact", "text = cardImpact.stripIntroductoryLabels()")
card = card.replace("text = news.summaryActionableTakeaway", "text = news.summaryActionableTakeaway.stripIntroductoryLabels()")

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(card)
print("Patched NewsItemCard.kt")

# 3. Patch NewsProcessorService.kt
with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "r") as f:
    proc = f.read()

proc = proc.replace(
    '"• Market Update: Latest developments impacting indices and policies\\n• Investor Takeaway: Adjust portfolio based on the latest macroeconomic news"',
    '"• Sector policy shift impacting market indices by ~2.5%\\n• Portfolio reallocation recommended based on updated guidance"'
)

with open("app/src/main/java/com/example/network/NewsProcessorService.kt", "w") as f:
    f.write(proc)
print("Patched NewsProcessorService.kt")

