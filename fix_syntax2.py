with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'r') as f:
    text = f.read()

text = text.replace('state.currentPolicy.financialYear = selectionOption', 'viewModel.selectFinancialYear(selectionOption)')

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'w') as f:
    f.write(text)
