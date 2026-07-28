with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

widget_code = """
@Composable
fun BullishBearishWidget(newsId: Int) {
    var hasVoted by remember { mutableStateOf(false) }
    var bullishVotes by remember { mutableStateOf(124) }
    var bearishVotes by remember { mutableStateOf(45) }
    
    val totalVotes = bullishVotes + bearishVotes
    val bullishPercent = if (totalVotes > 0) (bullishVotes.toFloat() / totalVotes * 100).toInt() else 0
    val bearishPercent = if (totalVotes > 0) 100 - bullishPercent else 0

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = "Community Sentiment",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (!hasVoted) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { bullishVotes++; hasVoted = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Bullish 🚀")
                }
                Button(
                    onClick = { bearishVotes++; hasVoted = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Bearish 📉")
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(bullishPercent.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$bullishPercent%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .weight(bearishPercent.toFloat().coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(Color(0xFFF44336)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$bearishPercent%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Bullish", fontSize = 10.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                Text("Bearish", fontSize = 10.sp, color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
            }
        }
    }
}
"""

if "fun BullishBearishWidget" not in content:
    content += "\n" + widget_code

# Inject into InshortsNewsCardItem
injection_point = """                    InshortsBulletPoint(
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFFA5D6A7),
                        label = "Action",
                        content = news.summaryActionableTakeaway
                    )
                }"""

replacement = """                    InshortsBulletPoint(
                        icon = Icons.Default.CheckCircle,
                        iconColor = Color(0xFFA5D6A7),
                        label = "Action",
                        content = news.summaryActionableTakeaway
                    )
                    
                    if (news.category.contains("Market", ignoreCase = true) || news.category.contains("Funds", ignoreCase = true)) {
                        BullishBearishWidget(newsId = news.id)
                    }
                }"""

content = content.replace(injection_point, replacement)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
