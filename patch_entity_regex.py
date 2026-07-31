import re

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    content = f.read()

pattern = r"fun FinancialNewsEntity\.getMergedKeyTakeaways\(\): String \{.*?\n\}"
replacement = """fun FinancialNewsEntity.getMergedKeyTakeaways(): String {
    val who = summaryWhoImpacted.trim().ifBlank { "• User Impacted: Salaried taxpayers, retail investors & cardholders" }
    val why = summaryText.trim().ifBlank { "• Why It matters: Key regulatory shift influencing yields and credit savings." }
    val benefit = financialImpactBullets?.trim()?.ifBlank { null } ?: "• Financial benefits: +₹12,500/yr savings via optimized tax deduction & cashbacks"

    val finalWho = if (who.contains("User Impacted", ignoreCase = true)) who else "• User Impacted: $who"
    val finalWhy = if (why.contains("Why It matters", ignoreCase = true) || why.contains("Why It Matters", ignoreCase = true)) why else "• Why It matters: $why"
    val finalBenefit = if (benefit.contains("Financial benefits", ignoreCase = true) || benefit.contains("Tangible Value", ignoreCase = true)) benefit else "• Financial benefits: $benefit"

    return "$finalWho\n$finalWhy\n$finalBenefit"
}"""

new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)
with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
    f.write(new_content)
