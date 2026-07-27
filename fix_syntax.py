with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'r') as f:
    text = f.read()

text = text.replace('viewModel.updateGrossIncome(newValue.filter) { it.isDigit() }', 'viewModel.updateGrossIncome(newValue.filter { it.isDigit() })')
text = text.replace('viewModel.updateGrossIncome(it.filter) { char -> char.isDigit() }', 'viewModel.updateGrossIncome(it.filter { char -> char.isDigit() })')
text = text.replace('viewModel.updateSec80C(it.filter) { char -> char.isDigit() }', 'viewModel.updateSec80C(it.filter { char -> char.isDigit() })')
text = text.replace('viewModel.updateSec80D(it.filter) { char -> char.isDigit() }', 'viewModel.updateSec80D(it.filter { char -> char.isDigit() })')
text = text.replace('viewModel.updateNps(it.filter) { char -> char.isDigit() }', 'viewModel.updateNps(it.filter { char -> char.isDigit() })')
text = text.replace('viewModel.updateHraLoan(it.filter) { char -> char.isDigit() }', 'viewModel.updateHraLoan(it.filter { char -> char.isDigit() })')

text = text.replace('val = it', 'viewModel.updateGrossIncome(it)')
# Look for line 361: 'val' cannot be reassigned.
# In Kotlin, `val` can't be reassigned, so maybe there's a typo like `state.grossIncomeInput = it` became `val = it`?
# Wait, let me check line 361.

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'w') as f:
    f.write(text)
