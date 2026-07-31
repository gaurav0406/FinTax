import re

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    content = f.read()

# We need to replace getMergedKeyTakeaways

old_method = """fun FinancialNewsEntity.getMergedKeyTakeaways(): String {
    val who = summaryWhoImpacted.stripIntroductoryLabels()
        .replace("•", "").replace("-", "").trim()
        .ifBlank { "Salaried taxpayers, retail investors & cardholders" }
    val rawWhat = summaryWhatHappened.stripIntroductoryLabels()
        .replace("•", "").replace("-", "").trim()
        .ifBlank { "Key regulatory shift influencing yields and credit savings." }
    val why = if (rawWhat.length > 130) rawWhat.take(127) + "..." else rawWhat

    val rawMetric = (keyMetrics ?: "").stripIntroductoryLabels().replace("•", "").replace("-", "").trim()
    val rawAction = summaryActionableTakeaway.stripIntroductoryLabels().replace("•", "").replace("-", "").trim()
    
    val finBenefit = when {
        rawMetric.isNotBlank() && rawAction.isNotBlank() -> "$rawMetric — $rawAction"
        rawMetric.isNotBlank() -> "$rawMetric net annual yield impact"
        rawAction.isNotBlank() -> "+15.0% Net Savings — $rawAction"
        else -> "+₹12,500/yr savings via optimized tax deduction & cashbacks"
    }

    return "• User Impacted: $who\n• Why It Matters: $why\n• Financial Benefits: $finBenefit"
}"""

new_method = """fun FinancialNewsEntity.getMergedKeyTakeaways(): String {
    val who = summaryWhoImpacted.trim().ifBlank { "• User Impacted: Salaried taxpayers, retail investors & cardholders" }
    val why = summaryText.trim().ifBlank { "• Why It matters: Key regulatory shift influencing yields and credit savings." }
    val benefit = financialImpactBullets?.trim()?.ifBlank { null } ?: "• Financial benefits: +₹12,500/yr savings via optimized tax deduction & cashbacks"

    val finalWho = if (who.contains("User Impacted", ignoreCase = true)) who else "• User Impacted: $who"
    val finalWhy = if (why.contains("Why It matters", ignoreCase = true) || why.contains("Why It Matters", ignoreCase = true)) why else "• Why It matters: $why"
    val finalBenefit = if (benefit.contains("Financial benefits", ignoreCase = true) || benefit.contains("Tangible Value", ignoreCase = true)) benefit else "• Financial benefits: $benefit"

    return "$finalWho\n$finalWhy\n$finalBenefit"
}"""

if old_method in content:
    content = content.replace(old_method, new_method)
else:
    print("Could not find old method exactly.")

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
    f.write(content)
