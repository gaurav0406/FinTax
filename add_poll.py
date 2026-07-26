import re

with open('app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt', 'r') as f:
    text = f.read()

imports = """import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.filled.Poll"""

if "import androidx.compose.material.icons.filled.Poll" not in text:
    text = text.replace("import androidx.compose.material.icons.filled.LocalFireDepartment", "import androidx.compose.material.icons.filled.LocalFireDepartment\n" + imports)

poll_ui = """
        // Automated Quiz / Poll Section (Runs every 12-24 hrs)
        Spacer(modifier = Modifier.height(16.dp))
        var votedOption by remember { mutableStateOf<Int?>(null) }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Poll, contentDescription = "Daily Poll", tint = MinimalPurplePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Daily Financial Pulse", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Do you think the new FY 26-27 proposed tax slabs will actually boost your net savings?", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                
                val options = listOf("Yes, absolutely", "No, it's mostly a reshuffle", "I need to calculate first")
                val results = listOf(0.45f, 0.35f, 0.20f) // Mock results
                
                options.forEachIndexed { index, option ->
                    val isSelected = votedOption == index
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MinimalPurpleLightContainer else MinimalSurfaceVariant)
                            .clickable { votedOption = index }
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(option, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) MinimalPurpleDark else TextPrimary)
                                if (votedOption != null) {
                                    Text("${(results[index] * 100).toInt()}%", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            if (votedOption != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { results[index] },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MinimalPurplePrimary,
                                    trackColor = Color.LightGray
                                )
                            }
                        }
                    }
                }
                if (votedOption != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1,245 votes • Ends in 6 hrs", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
"""

text = text.replace('            // News List as discussion topics\n            LazyColumn(', poll_ui + '            // News List as discussion topics\n            LazyColumn(')

with open('app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt', 'w') as f:
    f.write(text)
