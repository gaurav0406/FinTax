import re

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

# Replace curatedDeals definition with a parameter or dynamic mapping
new_header = """import com.example.data.FinancialNewsEntity

@Composable
fun DealsAndOffersTab(newsList: List<FinancialNewsEntity> = emptyList()) {
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitleToOpen by remember { mutableStateOf("Offer Details") }
    var selectedCategory by remember { mutableStateOf("All Deals") }
    
    val curatedDeals = remember(newsList) {
        val deals = newsList.filter { 
            it.category.contains("Credit", ignoreCase = true) ||
            it.category.contains("Deal", ignoreCase = true) ||
            it.category.contains("Offer", ignoreCase = true)
        }.map { news ->
            DealItem(
                id = news.id.toString(),
                brandName = news.sourceName ?: "Offer",
                title = news.title,
                description = news.summaryWhatHappened.ifBlank { news.text ?: "" },
                offerCode = null, // dynamic deals don't have this, unless extracted
                linkUrl = news.sourceUrl,
                imageUrl = news.imageUrl ?: "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=800&q=80",
                category = news.category
            )
        }
        deals
    }
"""

content = re.sub(r'val curatedDeals = listOf\([\s\S]*?@Composable\nfun DealsAndOffersTab\(\) \{\n    var webViewUrlToOpen by remember \{ mutableStateOf<String\?>\(null\) \}\n    var webViewTitleToOpen by remember \{ mutableStateOf\("Offer Details"\) \}\n    var selectedCategory by remember \{ mutableStateOf\("All Deals"\) \}', new_header, content)

# There is a problem that DealsItem needs to be kept in the file, and I just regexed out the curatedDeals and replaced the function signature. Let's make sure I didn't delete DealItem.
