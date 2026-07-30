import json, re

def strip_labels(text):
    if not isinstance(text, str) or not text:
        return text
    pattern = r'(?m)(^|\n)(•\s*)?(?:Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review|Market Update|Key Highlight|Practical Takeaway|Direct Cash Impact|Net Card Yield|Interest Yield|Loan EMI Impact|Operational Savings|Tax Incentive|Liquidity Boost|Expected Yield|Financial Gain|Championship Standing|Curriculum Shift|Streaming Rights|Infrastructure Boost|Tech Efficiency|Workflow Automation|Career Advantage|Ecosystem Growth|Job Creation|Reason for change):\s*'
    cleaned = re.sub(pattern, r'\1\2', text, flags=re.IGNORECASE)
    # Also remove inline labels
    cleaned = re.sub(r'(?i)\b(Key Update|Market Context|Source Report|Investor Takeaway|Monetary Outlook|Verify Details|Portfolio Review):\s*', '', cleaned)
    return cleaned.strip()

files = [
    "backend_pipeline/processed_scraped_data.json",
    "backend/processed_scraped_data.json",
    "app/src/main/assets/processed_scraped_data.json",
    "processed_scraped_data.json"
]

for file_path in files:
    try:
        with open(file_path, "r") as f:
            data = json.load(f)
        
        for item in data:
            if "reason" in item:
                item["reason"] = strip_labels(item["reason"])
            if "financial_impact" in item:
                item["financial_impact"] = strip_labels(item["financial_impact"])
            if "action" in item:
                item["action"] = strip_labels(item["action"])
            if "summary" in item:
                item["summary"] = strip_labels(item["summary"])
            if "summaryText" in item:
                item["summaryText"] = strip_labels(item["summaryText"])
            if "financialImpactBullets" in item:
                item["financialImpactBullets"] = strip_labels(item["financialImpactBullets"])
            if "summaryActionableTakeaway" in item:
                item["summaryActionableTakeaway"] = strip_labels(item["summaryActionableTakeaway"])
            if "summaryWhatHappened" in item:
                item["summaryWhatHappened"] = strip_labels(item["summaryWhatHappened"])
                
        with open(file_path, "w") as f:
            json.dump(data, f, indent=2)
        print(f"Cleaned {file_path}")
    except Exception as e:
        print(f"Skipped {file_path}: {e}")

