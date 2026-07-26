import re

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'r') as f:
    text = f.read()

replacement = """        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("700000" to "₹7L", "1000000" to "₹10L", "1500000" to "₹15L").forEach { (amount, label) ->
                val isSelected = grossIncomeInput == amount
                Surface(
                    onClick = { grossIncomeInput = amount },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MinimalPurplePrimary else Color.White,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MinimalBorder) else null,
                    modifier = Modifier.weight(1f).height(40.dp).testTag("preset_salary_$label")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MinimalPurpleDark
                            )
                        )
                    }
                }
            }
            
            // Manual entry field
            OutlinedTextField(
                value = grossIncomeInput,
                onValueChange = { grossIncomeInput = it.filter { char -> char.isDigit() } },
                placeholder = { Text("Manual", fontSize = 12.sp) },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalPurpleDark,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MinimalPurplePrimary,
                    unfocusedBorderColor = MinimalBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MinimalPurpleDark,
                    unfocusedTextColor = MinimalPurpleDark
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier.weight(1.2f).height(40.dp)
            )
        }"""

text = re.sub(r'Row\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*horizontalArrangement = Arrangement\.spacedBy\(8\.dp\)\s*\)\s*\{.*?Spacer\(modifier = Modifier\.height\(16\.dp\)\)', replacement + '\n\n        Spacer(modifier = Modifier.height(16.dp))', text, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'w') as f:
    f.write(text)
