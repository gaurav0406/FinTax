package com.example.network

import com.example.data.CommentEntity
import com.example.data.FinancialNewsEntity

object SamplePreloadedData {

    fun getInitialNewsList(): List<FinancialNewsEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            // --- CREDIT CARDS ---
            FinancialNewsEntity(
                id = 1,
                title = "HDFC Regalia & Millennia Credit Card Reward Point Devaluation",
                summaryWhatHappened = "HDFC Bank revised reward point redemption caps for Regalia Gold and Millennia credit cards, capping utility bill payment rewards to 2,000 points/month.",
                summaryWhoImpacted = "HDFC Regalia, Millennia, and Infinia cardholders spending over ₹15,000 on utility & telecom bills.",
                summaryActionableTakeaway = "🟢 Impact: Save or Shell Out? You shell out ₹350/mo more if paying high utility bills. Shift utility payments to Airtel Axis or Amazon Pay ICICI card to retain 5% cashback.",
                summaryText = "HDFC Bank revised reward point redemption caps for Regalia Gold and Millennia credit cards, capping utility bill payment rewards to 2,000 points/month. Shift utility payments to Airtel Axis or Amazon Pay ICICI card to retain 5% cashback.",
                category = "Credit Cards",
                financialActionUrl = "https://www.hdfcbank.com/personal/pay/cards/credit-cards",
                sourceUrl = "https://www.hdfcbank.com/personal/pay/cards/credit-cards/important-notices",
                sourceName = "HDFC Bank Official",
                imageUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Direct Fee Impact: Shell out ~₹350/mo in lost reward point redemptions\n• Alternative Card Gain: +5% cashback (₹300/mo) via Airtel Axis or Amazon Pay ICICI",
                publishedAt = now - (1000 * 60 * 15)
            ),
            FinancialNewsEntity(
                id = 2,
                title = "SBI Card Cashback Card Rule Change: Lounge Access Removed",
                summaryWhatHappened = "SBI Card discontinued complimentary domestic airport lounge access on the popular Cashback Credit Card to maintain 5% online cashback structure.",
                summaryWhoImpacted = "Frequent domestic flyers using SBI Cashback Credit Card for lounge visits.",
                summaryActionableTakeaway = "🔴 Impact: Shell Out More (~₹1,200 per lounge visit). Apply for lifetime-free ICICI Sapphiro or IDFC First Select card for lounge benefits.",
                summaryText = "SBI Card discontinued complimentary domestic airport lounge access on the popular Cashback Credit Card to maintain 5% online cashback structure. Apply for lifetime-free ICICI Sapphiro or IDFC First Select card for lounge benefits.",
                category = "Credit Cards",
                financialActionUrl = "https://www.sbicard.com/en/personal/credit-cards.page",
                sourceUrl = "https://www.sbicard.com/en/personal/credit-cards/cashback-sbi-card.page",
                sourceName = "SBI Card Official",
                imageUrl = "https://images.unsplash.com/photo-1563013544-824ae1b704d3?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Out of Pocket Outlay: ₹1,200 - ₹1,800 per lounge entry fee\n• Lifetime Free Card Benefit: ₹0 fee for ICICI Sapphiro / IDFC Select with 4 lounge passes/quarter",
                publishedAt = now - (1000 * 60 * 45)
            ),
            FinancialNewsEntity(
                id = 3,
                title = "Axis Bank Atlas & Horizon Credit Cards: Miles Transfer Threshold Hike",
                summaryWhatHappened = "Axis Bank updated edge miles transfer ratio for partner airlines (Vistara, Singapore Airlines, Qatar Airways) requiring minimum 5,000 point blocks.",
                summaryWhoImpacted = "Axis Atlas, Horizon, and Magnus Credit Card travel enthusiasts.",
                summaryActionableTakeaway = "🟢 Impact: Benefit: Transfer existing accrued Edge Miles before August 1 to avoid batch transfer restrictions.",
                summaryText = "Axis Bank updated edge miles transfer ratio for partner airlines requiring minimum 5,000 point blocks. Transfer existing accrued Edge Miles before August 1 to avoid batch transfer restrictions.",
                category = "Credit Cards",
                financialActionUrl = "https://www.axisbank.com/retail/cards/credit-card",
                sourceUrl = "https://www.axisbank.com/retail/cards/credit-card/axis-bank-atlas-credit-card",
                sourceName = "Axis Bank Official Portal",
                imageUrl = "https://images.unsplash.com/photo-1589758438368-0ad531db3366?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Transfer Threshold: Minimum block of 5,000 Edge Miles required\n• Valued Savings: Lock in ~₹7,500 flight value per 5,000 mile block before policy change",
                publishedAt = now - (1000 * 60 * 100)
            ),
            FinancialNewsEntity(
                id = 4,
                title = "ICICI Bank Amazon Pay Card Introduces 1% Fuel Surcharge Waiver Cap",
                summaryWhatHappened = "ICICI Bank capped the 1% fuel surcharge waiver to ₹400 per billing cycle across all HPCL and IOCL pumps in India.",
                summaryWhoImpacted = "Commuters spending over ₹40,000 on fuel monthly.",
                summaryActionableTakeaway = "🟢 Impact: Saves ₹400/mo fuel surcharge. Ensure fuel transaction values are between ₹500 and ₹4,000 to qualify.",
                summaryText = "ICICI Bank capped the 1% fuel surcharge waiver to ₹400 per billing cycle across all HPCL and IOCL pumps in India. Ensure fuel transaction values are between ₹500 and ₹4,000 to qualify.",
                category = "Credit Cards",
                financialActionUrl = "https://www.icicibank.com/personal-banking/cards/credit-card/amazon-pay-credit-card",
                sourceUrl = "https://www.icicibank.com/personal-banking/cards/credit-card",
                sourceName = "ICICI Bank Official",
                imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Surcharge Waiver Limit: Up to ₹400/mo (₹4,800/yr net savings)\n• Qualifying Transaction Bracket: Fuel payments between ₹500 and ₹4,000",
                publishedAt = now - (1000 * 60 * 150)
            ),

            // --- ITR & TAX ---
            FinancialNewsEntity(
                id = 5,
                title = "ITR Filing Deadline & New Section 87A Rebate Rules",
                summaryWhatHappened = "Income Tax Dept released updated Section 87A rules granting full tax rebate on taxable incomes up to ₹7 Lakhs under New Regime.",
                summaryWhoImpacted = "Salaried Class and individual taxpayers earning under ₹7.5 Lakhs annually.",
                summaryActionableTakeaway = "🟢 Impact: Saves up to ₹25,000 in income tax! File your ITR-1 before July 31 on the official e-filing portal.",
                summaryText = "Income Tax Dept released updated Section 87A rules granting full tax rebate on taxable incomes up to ₹7 Lakhs under New Regime. Saves up to ₹25,000 in income tax! File your ITR-1 before July 31 on the official e-filing portal.",
                category = "ITR & Tax",
                financialActionUrl = "https://eportal.incometax.gov.in",
                sourceUrl = "https://economictimes.indiatimes.com/wealth/tax/itr-filing-new-tax-regime-rules/articleshow/1029381.cms",
                sourceName = "Income Tax E-Filing Portal",
                imageUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Estimated Tax Savings: Full ₹25,000 rebate on taxable income ≤ ₹7 Lakhs\n• Monthly Cashflow Impact: +₹2,083/mo higher take-home pay",
                publishedAt = now - (1000 * 60 * 30)
            ),
            FinancialNewsEntity(
                id = 6,
                title = "NPS Tier-1 Extra ₹50,000 Deduction Under Section 80CCD(1B)",
                summaryWhatHappened = "CBDT re-confirmed additional ₹50,000 tax deduction under NPS Tier-1 is fully available over and above the ₹1.5 Lakh 80C limit.",
                summaryWhoImpacted = "Salaried Corporate Employees and Self-Employed Taxpayers in 20% & 30% tax brackets.",
                summaryActionableTakeaway = "🟢 Impact: Saves ₹15,600 annually in taxes for 30% slab earners! Allocate ₹50,000 in Tier-1 NPS via CRA portal.",
                summaryText = "CBDT re-confirmed additional ₹50,000 tax deduction under NPS Tier-1 is fully available over and above the ₹1.5 Lakh 80C limit. Saves ₹15,600 annually in taxes for 30% slab earners!",
                category = "ITR & Tax",
                financialActionUrl = "https://enps.nsdl.com/",
                sourceUrl = "https://eportal.incometax.gov.in",
                sourceName = "Income Tax Dept",
                imageUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Tax Savings (30% Bracket): ₹15,600 saved on ₹50,000 investment\n• Long-term Retirement Corpus: CAGR ~9.5-10.5% with government bond allocations",
                publishedAt = now - (1000 * 60 * 180)
            ),

            // --- CARS & EV ---
            FinancialNewsEntity(
                id = 7,
                title = "Tata Curvv EV Launched: 502km Range & 5-Star BNCAP Safety Rating",
                summaryWhatHappened = "Tata Motors launched the Curvv Coupe SUV starting at ₹9.99 Lakhs (ICE) and ₹17.49 Lakhs (EV). Delivers 502 km battery range and 5-Star BNCAP crash rating.",
                summaryWhoImpacted = "Automotive enthusiasts, EV buyers, and urban SUV seekers in India.",
                summaryActionableTakeaway = "🟢 Impact: Save ₹7,000/month on fuel costs vs Petrol SUV! Check Tata Motors portal for variant breakdown.",
                summaryText = "Tata Motors launched the Curvv Coupe SUV starting at ₹9.99 Lakhs (ICE) and ₹17.49 Lakhs (EV). Save ₹7,000/month on fuel costs vs Petrol SUV!",
                category = "Cars & EV",
                financialActionUrl = "https://ev.tatamotors.com/curvv/",
                sourceUrl = "https://www.autocarindia.com/car-news/tata-curvv-ev-launched-at-rs-1749-lakh-432501",
                sourceName = "Tata Motors Official",
                imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Operational Cost Savings: ~₹7,000/mo (₹84,000/yr) compared to Petrol SUV\n• Tax Benefit: Section 80EEB tax deduction up to ₹1.5 Lakhs on EV loan interest",
                publishedAt = now - (1000 * 60 * 60)
            ),
            FinancialNewsEntity(
                id = 8,
                title = "Mahindra Thar Roxx 5-Door Unveiled: Safety & 4x4 Efficiency Specs",
                summaryWhatHappened = "Mahindra revealed the Thar Roxx 5-door SUV with mStallion Turbo Petrol and mHawk Diesel engines, offering 15.2 kmpl fuel efficiency and level 2 ADAS.",
                summaryWhoImpacted = "Off-road enthusiasts, family SUV buyers, and automotive buyers.",
                summaryActionableTakeaway = "💡 Impact: Great feature set with Level 2 ADAS and panoramic sunroof starting at ₹12.99 Lakhs.",
                summaryText = "Mahindra revealed the Thar Roxx 5-door SUV with mStallion Turbo Petrol and mHawk Diesel engines, offering 15.2 kmpl fuel efficiency and level 2 ADAS.",
                category = "Cars & EV",
                financialActionUrl = "https://auto.mahindra.com/suv/thar-roxx",
                sourceUrl = "https://www.carwale.com/mahindra-cars/thar-roxx/",
                sourceName = "Mahindra Auto Portal",
                imageUrl = "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Price Efficiency: Starting at ₹12.99 Lakhs with Level 2 ADAS\n• Fuel Outlay: Estimated ₹9.8/km running cost at 15.2 kmpl mileage",
                publishedAt = now - (1000 * 60 * 210)
            ),

            // --- LOANS & FDs ---
            FinancialNewsEntity(
                id = 9,
                title = "SBI & HDFC Hike Senior Citizen FD Rates to 8.25 Percent",
                summaryWhatHappened = "Major public & private banks introduced special 444-day Fixed Deposit schemes yielding up to 8.25% p.a. for senior citizens and 7.75% for regular depositors.",
                summaryWhoImpacted = "Senior Citizens, retirees, and conservative investors seeking guaranteed fixed returns.",
                summaryActionableTakeaway = "🟢 Impact: Save & Earn! Earn ₹8,250 interest per ₹1 Lakh deposit annually. Lock in rates before rate cuts.",
                summaryText = "Major public & private banks introduced special 444-day Fixed Deposit schemes yielding up to 8.25% p.a. Earn ₹8,250 interest per ₹1 Lakh deposit annually.",
                category = "Loans & FDs",
                financialActionUrl = "https://www.sbi.co.in/web/interest-rates/deposit-rates",
                sourceUrl = "https://www.sbi.co.in",
                sourceName = "SBI Official Portal",
                imageUrl = "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Guaranteed Interest Yield: 8.25% p.a. (+₹8,250/yr per ₹1 Lakh deposit)\n• Tax Exemption: Up to ₹50,000 interest tax-free under Section 80TTB for seniors",
                publishedAt = now - (1000 * 60 * 120)
            ),
            FinancialNewsEntity(
                id = 10,
                title = "RBI MCLR Interest Rate Reset: Home Loan EMIs Updated",
                summaryWhatHappened = "State Bank of India & ICICI Bank adjusted 1-Year Marginal Cost of Funds Based Lending Rate (MCLR) by 5 bps.",
                summaryWhoImpacted = "Existing floating rate home loan borrowers on 1-year MCLR reset cycles.",
                summaryActionableTakeaway = "🔴 Impact: Shell Out ~₹320/month extra EMI per ₹50 Lakh loan. Switch to Repo-Linked Rate (EBLR) to save.",
                summaryText = "State Bank of India & ICICI Bank adjusted 1-Year MCLR by 5 bps. Shell Out ~₹320/month extra EMI per ₹50 Lakh loan. Switch to Repo-Linked Rate (EBLR) to save.",
                category = "Loans & FDs",
                financialActionUrl = "https://homeloans.sbi/",
                sourceUrl = "https://www.sbi.co.in",
                sourceName = "SBI Home Loans Portal",
                imageUrl = "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Cash Outflow Spike: +₹320/mo extra EMI on ₹50 Lakh tenure loan\n• Potential Savings: Save ~₹1,200/mo by converting from MCLR to EBLR benchmark",
                publishedAt = now - (1000 * 60 * 280)
            ),

            // --- MARKETS & MUTUAL FUNDS ---
            FinancialNewsEntity(
                id = 11,
                title = "SEBI Mandates T+0 Settlement for Liquid & Equity Mutual Funds",
                summaryWhatHappened = "SEBI rolled out same-day redemption payout framework for liquid and equity mutual fund schemes.",
                summaryWhoImpacted = "Retail Mutual Fund investors and Monthly SIP contributors in India.",
                summaryActionableTakeaway = "🟢 Impact: Liquidity Benefit! Receive redemption proceeds in your bank account on the same day instead of T+2 days.",
                summaryText = "SEBI rolled out same-day redemption payout framework for liquid and equity mutual fund schemes. Receive redemption proceeds in your bank account on the same day instead of T+2 days.",
                category = "Markets & Mutual Funds",
                financialActionUrl = "https://www.amfiindia.com",
                sourceUrl = "https://www.amfiindia.com",
                sourceName = "AMFI Official Portal",
                imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Liquidity Boost: Instant T+0 credit frees funds 48 hours earlier\n• Opportunity Cost Gain: Earn ~1.2% CAGR higher returns by eliminating idle settlement days",
                publishedAt = now - (1000 * 60 * 140)
            ),

            // --- RBI & POLICY ---
            FinancialNewsEntity(
                id = 12,
                title = "RBI Keeps Repo Rate Unchanged at 6.5 Percent",
                summaryWhatHappened = "RBI Monetary Policy Committee voted to hold repo rate steady at 6.5% to balance inflation control and growth.",
                summaryWhoImpacted = "Home loan borrowers, FD investors, and retail banking customers.",
                summaryActionableTakeaway = "💡 Impact: EMIs stay stable. Lock in peak Fixed Deposit interest rates now before rate cuts in late 2026.",
                summaryText = "RBI Monetary Policy Committee voted to hold repo rate steady at 6.5% to balance inflation control and growth. Lock in peak Fixed Deposit interest rates now before potential rate cuts.",
                category = "RBI & Policy",
                financialActionUrl = "https://rbi.org.in/Scripts/BS_PressReleaseDisplay.aspx",
                sourceUrl = "https://rbi.org.in/pressreleases/repo-rate-policy-2026",
                sourceName = "RBI Press Release",
                imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Stability Guarantee: EMI costs held constant across 2026 Q3/Q4\n• Yield Strategy: Secure 7.50% - 8.25% fixed rates before future interest rate cuts",
                publishedAt = now - (1000 * 60 * 200)
            ),

            // --- SPORTS ---
            FinancialNewsEntity(
                id = 13,
                title = "India vs England Test: Gill Century & WTC Final Standings",
                summaryWhatHappened = "Team India scored 445 runs in the first innings featuring a stellar century from Shubman Gill and 5 wickets by Jasprit Bumrah.",
                summaryWhoImpacted = "Cricket fans, fantasy sports gamers, and Indian sports followers.",
                summaryActionableTakeaway = "💡 Impact: India secures #1 position in WTC Standings. View official scorecard on BCCI portal.",
                summaryText = "Team India scored 445 runs in the first innings featuring a stellar century from Shubman Gill and 5 wickets by Jasprit Bumrah, securing top spot in WTC standings.",
                category = "Sports",
                financialActionUrl = "https://www.bcci.tv",
                sourceUrl = "https://www.espncricinfo.com/series/india-in-england-2026",
                sourceName = "BCCI Official",
                imageUrl = "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• WTC Point Table: India leads with 68.5% PCT\n• Fantasy Gaming Metric: 185 fantasy points accumulated by Shubman Gill",
                publishedAt = now - (1000 * 60 * 90)
            ),
            FinancialNewsEntity(
                id = 14,
                title = "UEFA Champions League Quarter-Finals: Thrilling 3-2 Comeback",
                summaryWhatHappened = "Real Madrid staged a dramatic 89th-minute comeback victory against Bayern Munich to reach the Champions League Semi-Finals.",
                summaryWhoImpacted = "Football enthusiasts, European league followers, and sports analysts.",
                summaryActionableTakeaway = "💡 Impact: Real Madrid advances to UCL Semi-Finals. Official highlights available on UEFA portal.",
                summaryText = "Real Madrid staged a dramatic 89th-minute comeback victory against Bayern Munich to reach the Champions League Semi-Finals with a 3-2 aggregate score.",
                category = "Sports",
                financialActionUrl = "https://www.uefa.com/uefachampionsleague/",
                sourceUrl = "https://www.bbc.com/sport/football/champions-league",
                sourceName = "UEFA Official Portal",
                imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Tournament Prize Pool Impact: €12.5M bonus for reaching semi-final stage\n• Global Viewership Metric: Over 180M live viewers worldwide",
                publishedAt = now - (1000 * 60 * 220)
            ),
            FinancialNewsEntity(
                id = 15,
                title = "Education Policy 2024: New Exams and Curriculum Updates",
                summaryWhatHappened = "The Ministry of Education has announced major policy changes, introducing a dual-board exam system and new vocational courses. Several entrance exams have also updated their syllabus.",
                summaryWhoImpacted = "Students, educators, and institutions adapting to the new curriculum framework.",
                summaryActionableTakeaway = "💡 Impact: Review the new syllabus and prepare for the dual-exam model. Stay tuned for further updates extracted online.",
                summaryText = "The Ministry of Education announced major policy changes, introducing a dual-board exam system, vocational courses, and updating entrance exam syllabi. Important updates are available for students and educators.",
                category = "Education",
                financialActionUrl = "https://www.education.gov.in/",
                sourceUrl = "https://www.education.gov.in/policy",
                sourceName = "Ministry of Education",
                imageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Academic Fee Subsidy: Up to 40% government subsidy on certified vocational skill modules\n• Exam Fee Reduction: Single registration fee covers dual board assessment attempts",
                publishedAt = now - (1000 * 60 * 250)
            ),
            // --- ENTERTAINMENT ---
            FinancialNewsEntity(
                id = 16,
                title = "Indian OTT Streaming Rights Soar To Record ₹450 Crore Deal",
                summaryWhatHappened = "Major streaming giants inked multi-year distribution deals for blockbuster Indian films and live sports events, driven by 35% growth in subscriber monetization.",
                summaryWhoImpacted = "Moviegoers, digital subscribers, and media investors.",
                summaryActionableTakeaway = "🎬 Impact: Compare OTT bundle subscription plans (JioCinema, Prime, Netflix) via telco recharges to save up to ₹2,400 annually.",
                summaryText = "Major streaming giants inked multi-year distribution deals for blockbuster Indian films and live sports events. Compare OTT bundle subscription plans via telco recharges to save up to ₹2,400 annually.",
                category = "Entertainment",
                financialActionUrl = "https://www.primevideo.com",
                sourceUrl = "https://variety.com/asia/news",
                sourceName = "Entertainment Tech Wire",
                imageUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Media Rights Valuation: ₹450 Crore record licensing agreement\n• Subscription Bundle Savings: ~₹200/mo net savings on aggregated OTT passes",
                publishedAt = now - (1000 * 60 * 20)
            ),
            // --- TECHNOLOGY INSIGHTS ---
            FinancialNewsEntity(
                id = 17,
                title = "India Semiconductor Mission Grants ₹12,000 Cr Fabrication Plant Expansion",
                summaryWhatHappened = "The Cabinet approved additional capital subsidies under the Semiconductor Mission for 3 local chip assembly & testing plants in Gujarat and Assam.",
                summaryWhoImpacted = "Hardware engineers, electronics supply chains, and tech sector equity investors.",
                summaryActionableTakeaway = "⚡ Impact: Boosts domestic electronics manufacturing and reduces smartphone component import costs by 15%.",
                summaryText = "The Cabinet approved additional capital subsidies under the Semiconductor Mission for local chip assembly & testing plants, boosting electronics manufacturing and lowering import costs.",
                category = "Technology Insights",
                financialActionUrl = "https://www.ism.gov.in",
                sourceUrl = "https://pib.gov.in",
                sourceName = "Ministry of Electronics & IT",
                imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Capital Subsidy Outlay: ₹12,000 Crore government grant allocation\n• Import Duty Efficiency: 12-15% cost reduction on local tech component sourcing",
                publishedAt = now - (1000 * 60 * 35)
            ),
            // --- AI & NEW HAPPENINGS ---
            FinancialNewsEntity(
                id = 18,
                title = "Generative AI Enterprise Adoption Hits 60% Among Indian Tech Firms",
                summaryWhatHappened = "NASSCOM survey reveals over 60% of IT and financial enterprise firms integrated LLM workflows for automated code generation, customer support, and fraud detection.",
                summaryWhoImpacted = "Software professionals, AI developers, and tech managers.",
                summaryActionableTakeaway = "🤖 Impact: Gain certification in GenAI frameworks to unlock 25-35% salary hike opportunities.",
                summaryText = "NASSCOM survey reveals over 60% of IT and enterprise firms integrated LLM workflows. Gain certification in GenAI frameworks to unlock 25-35% salary hike opportunities.",
                category = "AI & New Happenings",
                financialActionUrl = "https://nasscom.in/ai",
                sourceUrl = "https://nasscom.in/research",
                sourceName = "NASSCOM AI Pulse",
                imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Productivity Multiplier: 40% reduction in routine operational task duration\n• Career Salary Premium: +25-35% average compensation uplift for certified AI talent",
                publishedAt = now - (1000 * 60 * 10)
            ),
            // --- FINTECH & CRYPTO ---
            FinancialNewsEntity(
                id = 19,
                title = "RBI Expands Offline Digital Rupee CBDC for Low-Connectivity Transactions",
                summaryWhatHappened = "The Reserve Bank of India launched offline feature-phone capabilities for e-Rupee digital currency, enabling tap-and-pay transactions without active internet.",
                summaryWhoImpacted = "Merchant shops, rural consumers, and digital wallet users.",
                summaryActionableTakeaway = "💳 Impact: Zero transaction fee and instant merchant settlement on e-Rupee wallets. Download official RBI CBDC bank app to enroll.",
                summaryText = "RBI launched offline feature-phone capabilities for e-Rupee digital currency, enabling tap-and-pay transactions without active internet with zero transaction fees.",
                category = "FinTech & Crypto",
                financialActionUrl = "https://www.rbi.org.in/cbdc",
                sourceUrl = "https://www.rbi.org.in/scripts/BS_PressReleaseDisplay.aspx",
                sourceName = "RBI CBDC Portal",
                imageUrl = "https://images.unsplash.com/photo-1621416894569-0f39ed31d247?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Merchant Settlement Fee: 0% MDR vs standard 1.5% credit card processing fee\n• Offline Payment Limit: Up to ₹2,000 per transaction without cellular network",
                publishedAt = now - (1000 * 60 * 50)
            ),
            // --- STARTUP ECOSYSTEM ---
            FinancialNewsEntity(
                id = 20,
                title = "Indian Startup Funding Rebounds With \$1.8 Billion Raised in Q2",
                summaryWhatHappened = "Venture capital inflow into Indian startups increased by 42% quarter-on-quarter, led by mega rounds in FinTech, DeepTech, and Clean Energy sectors.",
                summaryWhoImpacted = "Founders, angel investors, job seekers, and venture analysts.",
                summaryActionableTakeaway = "🚀 Impact: Over 5,000 tech roles opened across Series B+ startups. Explore official hiring boards on Inc42 and YourStory.",
                summaryText = "Venture capital inflow into Indian startups increased by 42% quarter-on-quarter, led by mega rounds in FinTech and DeepTech, generating 5,000+ new tech roles.",
                category = "Startup Ecosystem",
                financialActionUrl = "https://startupindia.gov.in",
                sourceUrl = "https://inc42.com/reports",
                sourceName = "Startup Ecosystem Report",
                imageUrl = "https://images.unsplash.com/photo-1559136555-9303baea8ebd?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Total Quarterly Capital: \$1.8 Billion VC infusion\n• Hiring Market Metric: 5,000+ skilled tech and product vacancies created",
                publishedAt = now - (1000 * 60 * 65)
            ),
            // --- SMART INVESTING ---
            FinancialNewsEntity(
                id = 21,
                title = "SEBI Mandates Low-Cost Direct Plan Disclosure for Mutual Fund Investors",
                summaryWhatHappened = "SEBI required asset management companies to prominently display 10-year return gaps between Regular and Direct Mutual Fund plans on quarterly statements.",
                summaryWhoImpacted = "Mutual fund investors and SIP holders.",
                summaryActionableTakeaway = "📈 Impact: Switch from Regular to Direct Plans via Zerodha Coin or Groww to save ~1% per year in distributor commission (~₹3.5 Lakhs over 15 yrs on ₹10k SIP).",
                summaryText = "SEBI required mutual funds to prominently display return gaps between Regular and Direct Plans. Switch to Direct Plans to save ~1% per year in commissions (~₹3.5 Lakhs over 15 yrs).",
                category = "Smart Investing",
                financialActionUrl = "https://www.sebi.gov.in",
                sourceUrl = "https://www.amfiindia.com",
                sourceName = "SEBI Official Circular",
                imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Expense Ratio Savings: ~1.0% p.a. saved on distributor commissions\n• Long-Term Compounding Gain: +₹3,50,000 wealth creation over 15 years on ₹10,000/mo SIP",
                publishedAt = now - (1000 * 60 * 80)
            ),
            // --- PERSONAL FINANCE ---
            FinancialNewsEntity(
                id = 22,
                title = "Health Insurance Portability Rules: No Waiting Period Reset On Switch",
                summaryWhatHappened = "IRDAI notified updated health insurance guidelines ensuring pre-existing disease (PED) waiting period credit is fully transferred when switching insurers.",
                summaryWhoImpacted = "Policyholders upgrading health insurance coverage or changing insurers.",
                summaryActionableTakeaway = "💡 Impact: Port your policy 45 days before renewal to secure higher sum insured without losing accrued 3-year waiting period credits.",
                summaryText = "IRDAI notified updated health insurance guidelines ensuring pre-existing disease waiting period credit is fully transferred when switching insurers. Port your policy 45 days before renewal.",
                category = "Personal Finance",
                financialActionUrl = "https://www.irdai.gov.in",
                sourceUrl = "https://policybazaar.com/health-insurance",
                sourceName = "IRDAI Consumer Affairs",
                imageUrl = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=1200&q=80",
                financialImpactBullets = "• Waiting Period Credit Transfer: 100% credit retained for completed 36 months\n• Premium Cost Optimization: Up to ₹4,500/yr saved by comparing ported plans",
                publishedAt = now - (1000 * 60 * 110)
            )
        ) + generateExtraNews(now)
    }

    private fun generateExtraNews(baseTime: Long): List<FinancialNewsEntity> {
        val categories = listOf(
            "Credit Cards", "ITR & Tax", "Loans & FDs", "Markets & Mutual Funds", 
            "RBI & Policy", "Entertainment", "Technology Insights", "AI & New Happenings", 
            "FinTech & Crypto", "Startup Ecosystem", "Smart Investing", "Personal Finance", 
            "Sports", "Cars & EV", "Education"
        )
        val generatedList = mutableListOf<FinancialNewsEntity>()
        
        for (i in 23..60) {
            val category = categories[i % categories.size]
            generatedList.add(
                FinancialNewsEntity(
                    id = i,
                    title = "Key Changes in $category",
                    summaryWhatHappened = "Recent developments in $category indicate significant shifts in the market. Experts are advising users to closely monitor the trends.",
                    summaryWhoImpacted = "Investors and consumers actively engaged in $category and related financial products.",
                    summaryActionableTakeaway = "💡 Impact: Stay informed. Review your current portfolio or spending habits and adjust according to the latest $category updates to maximize benefits and minimize costs.",
                    summaryText = "Recent developments in $category indicate significant shifts. Review your current portfolio and adjust according to the latest $category updates to maximize benefits.",
                    category = category,
                    financialActionUrl = "https://www.example.com",
                    sourceUrl = "https://www.example.com/news/$i",
                    sourceName = "FinTax Updates",
                    imageUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?auto=format&fit=crop&w=1200&q=80",
                    financialImpactBullets = when (category) {
                        "ITR & Tax" -> "• Est. Tax Savings: ₹15,600 - ₹25,000/yr for ₹7L-15L bracket\n• Cashflow Impact: +₹2,083/mo net take-home salary boost"
                        "Credit Cards" -> "• Direct Cash Impact: -₹350/mo on utility caps or +5% (₹400/mo) on fuel\n• Net Card Yield: ~₹4,800/yr optimized cashback return"
                        "Loans & FDs" -> "• Interest Yield: 8.25% p.a. (+₹8,250/yr per ₹1L deposit)\n• Loan EMI Impact: +₹320/mo on ₹50L Home Loan reset"
                        "Markets & Mutual Funds" -> "• Liquidity Boost: T+0 payout frees funds 48 hrs faster\n• Expected Yield: +1.2% CAGR boost from faster reinvestment"
                        "Cars & EV" -> "• Operational Savings: ~₹7,000/mo (₹84,000/yr) vs Petrol vehicle\n• Tax Incentive: Sec 80EEB tax deduction up to ₹1.5 Lakhs"
                        "FinTech & Crypto" -> "• Transaction Velocity: Instant cross-border settlement with 0% fee\n• Digital Rupee CBDC: 1% cashback on offline wallet UPI payments"
                        "Smart Investing" -> "• Algo Trading Yield: +3.8% alpha over benchmark index\n• Expense Ratio Savings: 0.15% direct plan low-cost SIP advantage"
                        "Personal Finance" -> "• Emergency Fund Security: 6-month liquidity buffer preserved\n• Health Insurance Benefit: ₹50,000 tax deduction under Sec 80D"
                        "Sports" -> "• Championship Standing: India leads WTC table with strong performance\n• Key Highlight: Record-breaking individual and team statistics"
                        "Education" -> "• Curriculum Shift: Dual-board exam structure & updated entrance syllabi\n• Practical Takeaway: Skill integration across vocational streams"
                        "Entertainment" -> "• Distribution Milestone: Record multi-platform streaming rights agreement\n• Viewership Impact: Broader audience reach and digital catalog expansion"
                        "Technology Insights" -> "• Infrastructure Boost: Domestic semiconductor manufacturing expansion\n• Tech Efficiency: Lower reliance on component imports"
                        "AI & New Happenings" -> "• Workflow Automation: Accelerated developer productivity & AI deployment\n• Skill Demand: High career opportunities for generative AI specialists"
                        "Startup Ecosystem" -> "• Ecosystem Growth: Expanded funding rounds for tech ventures\n• Job Creation: High demand for skilled software engineering talent"
                        else -> "• Key Highlight: Major developments and strategic updates in this domain\n• Practical Takeaway: Essential insights and core knowledge for readers"
                    },
                    publishedAt = baseTime - (1000 * 60 * 300) - (i * 1000 * 60 * 60) // Older than the base items
                )
            )
        }
        return generatedList
    }

    fun getInitialComments(): List<CommentEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            CommentEntity(
                id = 1,
                newsId = 1,
                userName = "Gaurav Sharma",
                userCity = "Mumbai",
                commentText = "The HDFC utility cap to 2000 points hurts. I am shifting my electric bill payment to Airtel Axis!",
                upvotes = 18,
                timestamp = now - (1000 * 60 * 25)
            ),
            CommentEntity(
                id = 2,
                newsId = 1,
                parentCommentId = 1,
                userName = "Priya Sharma",
                userCity = "Delhi",
                commentText = "@Gaurav Sharma Airtel Axis gives 10% on utility up to ₹250/mo. Good alternative!",
                taggedUser = "Gaurav Sharma",
                upvotes = 12,
                timestamp = now - (1000 * 60 * 15)
            ),
            CommentEntity(
                id = 3,
                newsId = 5,
                userName = "Amitabh Verma",
                userCity = "Mumbai",
                commentText = "Extremely helpful clarification on Sec 87A rebate! Saved ₹25,000 on my tax filing today.",
                upvotes = 22,
                timestamp = now - (1000 * 60 * 18)
            ),
            CommentEntity(
                id = 4,
                newsId = 7,
                userName = "Rahul Dravid Fan",
                userCity = "Bangalore",
                commentText = "502km range on Tata Curvv EV is solid. Real world 390km is enough for Bangalore to Mysore trips!",
                upvotes = 31,
                timestamp = now - (1000 * 60 * 45)
            )
        )
    }
}

