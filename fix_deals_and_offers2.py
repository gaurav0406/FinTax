with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "r") as f:
    content = f.read()

start_idx = content.find("val curatedDeals = listOf(")
end_idx = content.find("@Composable\nfun DealsAndOffersTab() {")

if start_idx != -1 and end_idx != -1:
    before = content[:start_idx]
    after = content[end_idx + len("@Composable\nfun DealsAndOffersTab() {"):]
    
    new_code = """import com.example.data.FinancialNewsEntity

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
                offerCode = null,
                linkUrl = news.sourceUrl,
                imageUrl = news.imageUrl ?: "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=800&q=80",
                category = news.category
            )
        }
        deals
    }
"""
    content = before + new_code + after

with open("app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt", "w") as f:
    f.write(content)
