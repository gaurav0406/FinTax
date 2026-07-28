import re

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "r") as f:
    content = f.read()

new_fields = """
    val imageUrl: String? = null,
    val financialImpactBullets: String? = null,
    val keyMetrics: String? = null,
    val jargonTerms: String? = null,
    val publishedAt: Long = System.currentTimeMillis(),
"""

content = re.sub(r'val imageUrl: String\? = null,\s*val financialImpactBullets: String\? = null,\s*val publishedAt: Long = System\.currentTimeMillis\(\),', new_fields.strip() + ",", content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/FinancialNewsEntity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/data/AppDatabase.kt", "r") as f:
    db_content = f.read()
db_content = re.sub(r'version = \d+', 'version = 12', db_content)
with open("app/src/main/java/com/example/data/AppDatabase.kt", "w") as f:
    f.write(db_content)

