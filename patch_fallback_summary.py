with open("backend_pipeline/news_scraper_cron.py", "r") as f:
    content = f.read()

target_start = "def generate_fallback_llm_summary(item: dict) -> dict:"
target_end = "def push_to_supabase_rest(records: list):"

idx_start = content.find(target_start)
idx_end = content.find(target_end)

new_func = r"""def generate_fallback_llm_summary(item: dict) -> dict:
    title = item["title"]
    category = item["category"]
    raw_text = item.get("text", title)
    source = item.get("sourceName", "Financial Feed")
    
    sentences = [s.strip() for s in re.split(r'[.!?]+', raw_text) if len(s.strip()) > 15]
    first_sentence = sentences[0] if sentences else title
    second_sentence = sentences[1] if len(sentences) > 1 else "This development brings key policy and market updates for consumers."
    third_sentence = sentences[2] if len(sentences) > 2 else "Stakeholders are advised to monitor official announcements closely."

    summary = first_sentence + ". " + second_sentence + ". " + third_sentence
    reason_bullets = "• Key Update: " + first_sentence + "\n• Market Context: " + second_sentence + "\n• Source Report: Published via " + source
    financial_impact = "• Monetary Outlook: Direct cost and rate adjustments being evaluated across " + category + ".\n• Investor Takeaway: Assess portfolio alignment and review official notices."
    action_bullets = "• Verify Details: Check official guidelines issued by regulatory authorities.\n• Portfolio Review: Adjust allocation or rewards strategy according to latest updates."

    return {
        "id": item["id"],
        "title": title[:250],
        "summary": summary[:1000],
        "who_impacted": "Retail Investors, Salaried Professionals & " + category + " Consumers",
        "reason": reason_bullets,
        "financial_impact": financial_impact,
        "action": action_bullets,
        "category": category,
        "topic_cluster": "Latest Updates"
    }

"""

if idx_start != -1 and idx_end != -1:
    content = content[:idx_start] + new_func + content[idx_end:]
    with open("backend_pipeline/news_scraper_cron.py", "w") as f:
        f.write(content)
    print("Fallback generator replaced with raw string successfully.")
else:
    print("Indices not found!")
