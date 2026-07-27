import re

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'r') as f:
    text = f.read()

# 1. Update imports
imports = """import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.ui.viewmodels.TaxCalculatorViewModel
import com.example.ui.viewmodels.TaxCalculatorState"""
text = re.sub(r'import java\.util\.Locale', 'import java.util.Locale\n' + imports, text)

# 2. Update composable signature and state variables
old_sig = r'fun TaxCalculatorTab\(\s*modifier: Modifier = Modifier\s*\) \{.*?val recommendedRegime by remember\(oldTaxResult, newTaxResult\) \{\s*derivedStateOf \{\s*if \(newTaxResult\.totalTaxPayable <= oldTaxResult\.totalTaxPayable\) "New Regime" else "Old Regime"\s*\}\s*\}'

new_sig = """fun TaxCalculatorTab(
    modifier: Modifier = Modifier,
    viewModel: TaxCalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    val oldTaxResult = viewModel.calculateOldRegimeTax()
    val newTaxResult = viewModel.calculateNewRegimeTax()
    
    val taxSavings = kotlin.math.abs(oldTaxResult.totalTaxPayable - newTaxResult.totalTaxPayable)
    val recommendedRegime = if (newTaxResult.totalTaxPayable <= oldTaxResult.totalTaxPayable) "New Regime" else "Old Regime"
    
    var expandedFY by remember { mutableStateOf(false) }
    val fyOptions = listOf("FY 2025-26", "FY 2026-27 (Proposed)")"""
text = re.sub(old_sig, new_sig, text, flags=re.DOTALL)

# 3. Replace variable usage with state.*
text = text.replace('financialYear', 'state.currentPolicy.financialYear')
text = text.replace('grossIncomeInput', 'state.grossIncomeInput')
text = text.replace('sec80CInput', 'state.sec80CInput')
text = text.replace('sec80DInput', 'state.sec80DInput')
text = text.replace('npsInput', 'state.npsInput')
text = text.replace('hraLoanInput', 'state.hraLoanInput')
text = text.replace('isSalaried', 'state.isSalaried')

# 4. Replace variable assignment with viewModel.update*
text = re.sub(r'state\.grossIncomeInput = ([a-zA-Z0-9_.]+)', r'viewModel.updateGrossIncome(\1)', text)
text = re.sub(r'state\.sec80CInput = ([a-zA-Z0-9_.]+(?:\.filter \{.*?\})?)', r'viewModel.updateSec80C(\1)', text)
text = re.sub(r'state\.sec80DInput = ([a-zA-Z0-9_.]+(?:\.filter \{.*?\})?)', r'viewModel.updateSec80D(\1)', text)
text = re.sub(r'state\.npsInput = ([a-zA-Z0-9_.]+(?:\.filter \{.*?\})?)', r'viewModel.updateNps(\1)', text)
text = re.sub(r'state\.hraLoanInput = ([a-zA-Z0-9_.]+(?:\.filter \{.*?\})?)', r'viewModel.updateHraLoan(\1)', text)
text = re.sub(r'state\.isSalaried = (true|false)', r'viewModel.toggleSalaried(\1)', text)

# For dropdown select
text = re.sub(r'state\.currentPolicy\.financialYear = it', r'viewModel.selectFinancialYear(it)', text)

# Update onValueChange manual replacements just in case
text = text.replace('viewModel.updateGrossIncome(it.filter { char -> char.isDigit() })', 'viewModel.updateGrossIncome(it.filter { char -> char.isDigit() })')
text = text.replace('viewModel.updateGrossIncome(newValue.filter { it.isDigit() })', 'viewModel.updateGrossIncome(newValue.filter { it.isDigit() })')

# 5. Remove old models
text = re.sub(r'// --- DYNAMIC TAX CONFIGURATION MODELS ---.*?(?=$)', '', text, flags=re.DOTALL)
text = re.sub(r'data class TaxResult\(.*?\)', '', text, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt', 'w') as f:
    f.write(text)
