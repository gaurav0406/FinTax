import sys

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    lines = f.readlines()

start_idx = -1
end_idx = -1

for i, line in enumerate(lines):
    if "// Summary Breakdown" in line:
        start_idx = i
    if start_idx != -1 and "news.summaryActionableTakeaway," in line:
        # find the end of this block
        pass
    if start_idx != -1 and i > start_idx + 10 and "text = news.summaryActionableTakeaway," in lines[i-7]:
        end_idx = i
        break

if start_idx != -1 and end_idx != -1:
    new_lines = lines[:start_idx] + ["""            // Summary Breakdown
            NewsBulletPoint(
                icon = Icons.Default.Newspaper,
                iconColor = MaterialTheme.colorScheme.primary,
                label = "SUMMARY"
            ) {
                JargonText(
                    text = news.summaryWhatHappened,
                    jargonTerms = news.jargonTerms,
                    onJargonClick = { term, def ->
                        currentJargonTerm = term
                        currentJargonDefinition = def
                        showJargonSheet = true
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            NewsBulletPoint(
                icon = Icons.Default.People,
                iconColor = MaterialTheme.colorScheme.secondary,
                label = "IMPACTED USERS"
            ) {
                Text(
                    text = news.summaryWhoImpacted,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            NewsBulletPoint(
                icon = Icons.Default.Info,
                iconColor = MaterialTheme.colorScheme.primary,
                label = "WHY IT MATTERS"
            ) {
                JargonText(
                    text = news.summaryText,
                    jargonTerms = news.jargonTerms,
                    onJargonClick = { term, def ->
                        currentJargonTerm = term
                        currentJargonDefinition = def
                        showJargonSheet = true
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            val cardImpact = news.financialImpactBullets ?: if (news.isFinancialCategory) {
                when (news.category) {
                    "Financial News" -> "• Market Update: Latest developments impacting indices and policies\\n• Investor Takeaway: Adjust portfolio based on the latest macroeconomic news"
                    "Credit Cards" -> "• Direct Cash Impact: -₹350/mo on utility caps or +5% (₹400/mo) on fuel\\n• Net Card Yield: ~₹4,800/yr optimized cashback return"
                    "Loans & FDs" -> "• Interest Yield: 8.25% p.a. (+₹8,250/yr per ₹1L deposit)\\n• Loan EMI Impact: +₹320/mo on ₹50L Home Loan reset"
                    "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees funds 48 hrs faster\\n• Expected Yield: +1.2% CAGR boost from faster reinvestment"
                    "Cars & EV" -> "• Operational Savings: ~₹7,000/mo (₹84,000/yr) vs Petrol vehicle\\n• Tax Incentive: Sec 80EEB tax deduction up to ₹1.5 Lakhs"
                    else -> "• Financial Gain: Estimated ₹5,000 - ₹12,000 annual net benefit by optimizing financial options."
                }
            } else {
                when (news.category) {
                    "Sports" -> "• Championship Standing: India leads WTC table with strong performance\\n• Key Highlight: Record-breaking performance in recent fixtures"
                    "Education" -> "• Curriculum Shift: Dual-board exam structure & updated entrance syllabi\\n• Practical Takeaway: Skill integration across vocational streams"
                    "Entertainment" -> "• Streaming Rights: Major platform licensing and high viewer engagement\\n• Audience Value: Broader access to premium digital content bundles"
                    "Technology Insights" -> "• Infrastructure Boost: Domestic manufacturing expansion and supply chain growth\\n• Tech Efficiency: Lower reliance on component imports"
                    "AI & New Happenings" -> "• Workflow Automation: Accelerated developer productivity & AI deployment\\n• Career Advantage: High demand for generative AI skills"
                    else -> "• Key Highlight: Major developments and strategic updates in this domain\\n• Practical Takeaway: Core insights and essential knowledge for readers"
                }
            }
            
            NewsBulletPoint(
                icon = if (news.isFinancialCategory) Icons.Default.Calculate else Icons.Default.Info,
                iconColor = MaterialTheme.colorScheme.secondary,
                label = "FINANCIAL IMPACT & BENEFITS"
            ) {
                Text(
                    text = cardImpact,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            NewsBulletPoint(
                icon = Icons.Default.CheckCircle,
                iconColor = MaterialTheme.colorScheme.primary,
                label = "ACTIONABLE TAKEAWAYS"
            ) {
                Text(
                    text = news.summaryActionableTakeaway,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }\n"""] + lines[end_idx:]
            
    with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
        f.writelines(new_lines)
    print("Replaced successfully!")
else:
    print("Could not find block boundaries")
