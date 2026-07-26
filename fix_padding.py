import re

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'r') as f:
    text = f.read()

text = text.replace('onValueChange = { grossIncomeInput = it.filter { char -> char.isDigit() } },', 'onValueChange = { newValue -> grossIncomeInput = newValue.filter { it.isDigit() } },')
text = text.replace('contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),', '')

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'w') as f:
    f.write(text)
