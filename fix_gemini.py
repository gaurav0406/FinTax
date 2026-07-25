import re

with open('app/src/main/java/com/example/network/GeminiNewsService.kt', 'r') as f:
    text = f.read()

text = text.replace(
    '"Point 1: What happened (1-2 concise sentences)",',
    '"1-2 concise sentences describing what happened. DO NOT prefix with Point 1 or What happened.",'
)
text = text.replace(
    '"Point 2: Who is impacted e.g. Salaried Class, Senior Citizens, Taxpayers (1 sentence)",',
    '"1 sentence describing who is impacted (e.g. Salaried Class). DO NOT prefix with Point 2 or Who is impacted.",'
)
text = text.replace(
    '"Point 3: Actionable Takeaway e.g. File ITR-1 before July 31, Link Aadhaar (1 sentence)"',
    '"1 sentence describing the actionable takeaway. DO NOT prefix with Point 3 or Actionable Takeaway."'
)

text = text.replace(
    'val p1 = summaryArray?.optString(0) ?: "What Happened: Important financial update."',
    'val p1 = summaryArray?.optString(0) ?: "Important financial update regarding the latest guidelines."'
)
text = text.replace(
    'val p2 = summaryArray?.optString(1) ?: "Who is Impacted: Salaried taxpayers & investors."',
    'val p2 = summaryArray?.optString(1) ?: "Salaried taxpayers and general investors."'
)
text = text.replace(
    'val p3 = summaryArray?.optString(2) ?: "Actionable Takeaway: Verify guidelines on the official portal."',
    'val p3 = summaryArray?.optString(2) ?: "Verify details on the official portal and consult a financial advisor."'
)

with open('app/src/main/java/com/example/network/GeminiNewsService.kt', 'w') as f:
    f.write(text)
