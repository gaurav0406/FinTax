import re

with open('app/src/main/java/com/example/network/NewsProcessorService.kt', 'r') as f:
    text = f.read()

new_prompt = """                    You are an expert financial news summarizer. Extract and structure the following news into this exact JSON format. Keep it concise.
                    News: $rawText
                    
                    Respond ONLY with JSON:
                    {
                      "summary": "Provide a 6 to 7-line summary of the news.",
                      "impacted_users": "Provide 2 to 3 lines explaining who are the users impacted directly or indirectly.",
                      "reason": "Provide 2 to 3 lines explaining why the government or entity has taken a decision to make these changes.",
                      "financial_impact": "What is the financial impact or the benefits users can gain? Use crisp, quantifiable numbers and bullet points.",
                      "action": "Provide actionable steps (2 to 3 lines) users should take based on this to avoid risk and get the most from it.",
                      "category": "One of: ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"
                    }"""

# Need to replace the prompt block
text = re.sub(r'You are an expert financial news summarizer.*?"category": "One of: ITR & Tax, Credit Cards, Loans & FDs, Markets & Mutual Funds, FinTech & Crypto, Startup Ecosystem"\s*\}', new_prompt, text, flags=re.DOTALL)

with open('app/src/main/java/com/example/network/NewsProcessorService.kt', 'w') as f:
    f.write(text)
