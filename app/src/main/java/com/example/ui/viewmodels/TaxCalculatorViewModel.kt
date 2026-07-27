package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

data class TaxSlab(val min: Double, val max: Double?, val rate: Double)

data class TaxRegimeConfig(
    val standardDeduction: Double,
    val rebateLimit: Double,
    val slabs: List<TaxSlab>
)

data class TaxPolicyConfig(
    val financialYear: String,
    val oldRegime: TaxRegimeConfig,
    val newRegime: TaxRegimeConfig
)

data class TaxResult(
    val netTaxableIncome: Double,
    val baseTax: Double,
    val cess: Double,
    val totalTaxPayable: Double,
    val effectiveTaxRate: Double
)

data class TaxCalculatorState(
    val currentPolicy: TaxPolicyConfig = getDefaultPolicy(),
    val isSalaried: Boolean = true,
    val grossIncomeInput: String = "1200000",
    val sec80CInput: String = "150000",
    val sec80DInput: String = "25000",
    val npsInput: String = "50000",
    val hraLoanInput: String = "100000"
)

fun getDefaultPolicy(): TaxPolicyConfig {
    // Detect financial year dynamically based on current date
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    
    // FY 25-26 logic for now, or 26-27 if we are past April 2026
    val is2627 = if (year > 2026 || (year == 2026 && month >= Calendar.APRIL)) true else false
    
    return fetchTaxPolicyConfig(if (is2627) "26-27" else "25-26")
}

fun fetchTaxPolicyConfig(financialYear: String): TaxPolicyConfig {
    if (financialYear.contains("26-27")) {
        return TaxPolicyConfig(
            financialYear = "FY 2026-27 (Proposed)",
            oldRegime = TaxRegimeConfig(
                standardDeduction = 50000.0,
                rebateLimit = 500000.0,
                slabs = listOf(
                    TaxSlab(0.0, 250000.0, 0.0),
                    TaxSlab(250000.0, 500000.0, 0.05),
                    TaxSlab(500000.0, 1000000.0, 0.20),
                    TaxSlab(1000000.0, null, 0.30)
                )
            ),
            newRegime = TaxRegimeConfig(
                standardDeduction = 100000.0,
                rebateLimit = 750000.0,
                slabs = listOf(
                    TaxSlab(0.0, 400000.0, 0.0),
                    TaxSlab(400000.0, 800000.0, 0.05),
                    TaxSlab(800000.0, 1200000.0, 0.10),
                    TaxSlab(1200000.0, 1600000.0, 0.15),
                    TaxSlab(1600000.0, 2000000.0, 0.20),
                    TaxSlab(2000000.0, null, 0.30)
                )
            )
        )
    } else {
        return TaxPolicyConfig(
            financialYear = "FY 2025-26",
            oldRegime = TaxRegimeConfig(
                standardDeduction = 50000.0,
                rebateLimit = 500000.0,
                slabs = listOf(
                    TaxSlab(0.0, 250000.0, 0.0),
                    TaxSlab(250000.0, 500000.0, 0.05),
                    TaxSlab(500000.0, 1000000.0, 0.20),
                    TaxSlab(1000000.0, null, 0.30)
                )
            ),
            newRegime = TaxRegimeConfig(
                standardDeduction = 75000.0,
                rebateLimit = 700000.0,
                slabs = listOf(
                    TaxSlab(0.0, 300000.0, 0.0),
                    TaxSlab(300000.0, 700000.0, 0.05),
                    TaxSlab(700000.0, 1000000.0, 0.10),
                    TaxSlab(1000000.0, 1200000.0, 0.15),
                    TaxSlab(1200000.0, 1500000.0, 0.20),
                    TaxSlab(1500000.0, null, 0.30)
                )
            )
        )
    }
}

class TaxCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TaxCalculatorState())
    val uiState: StateFlow<TaxCalculatorState> = _uiState.asStateFlow()

    fun updateGrossIncome(income: String) {
        _uiState.update { it.copy(grossIncomeInput = income) }
    }

    fun updateSec80C(amount: String) {
        _uiState.update { it.copy(sec80CInput = amount) }
    }

    fun updateSec80D(amount: String) {
        _uiState.update { it.copy(sec80DInput = amount) }
    }

    fun updateNps(amount: String) {
        _uiState.update { it.copy(npsInput = amount) }
    }

    fun updateHraLoan(amount: String) {
        _uiState.update { it.copy(hraLoanInput = amount) }
    }

    fun toggleSalaried(isSalaried: Boolean) {
        _uiState.update { it.copy(isSalaried = isSalaried) }
    }

    fun selectFinancialYear(fy: String) {
        _uiState.update { it.copy(currentPolicy = fetchTaxPolicyConfig(fy)) }
    }

    fun calculateOldRegimeTax(): TaxResult {
        val state = uiState.value
        val config = state.currentPolicy.oldRegime
        
        val grossIncome = state.grossIncomeInput.toDoubleOrNull() ?: 0.0
        val sec80C = (state.sec80CInput.toDoubleOrNull() ?: 0.0).coerceAtMost(150000.0)
        val sec80D = (state.sec80DInput.toDoubleOrNull() ?: 0.0).coerceAtMost(75000.0)
        val nps = (state.npsInput.toDoubleOrNull() ?: 0.0).coerceAtMost(50000.0)
        val hraLoan = state.hraLoanInput.toDoubleOrNull() ?: 0.0
        
        val deductions = sec80C + sec80D + nps + hraLoan
        return calculateDynamicTax(grossIncome, deductions, state.isSalaried, config)
    }

    fun calculateNewRegimeTax(): TaxResult {
        val state = uiState.value
        val config = state.currentPolicy.newRegime
        
        val grossIncome = state.grossIncomeInput.toDoubleOrNull() ?: 0.0
        return calculateDynamicTax(grossIncome, 0.0, state.isSalaried, config)
    }

    private fun calculateDynamicTax(
        grossIncome: Double,
        totalDeductions: Double,
        isSalaried: Boolean,
        config: TaxRegimeConfig
    ): TaxResult {
        val stdDeduction = if (isSalaried) config.standardDeduction else 0.0
        val taxableIncome = (grossIncome - totalDeductions - stdDeduction).coerceAtLeast(0.0)

        if (taxableIncome <= config.rebateLimit) {
            return TaxResult(taxableIncome, 0.0, 0.0, 0.0, 0.0)
        }

        var baseTax = 0.0
        var remainingIncome = taxableIncome

        for (slab in config.slabs) {
            if (taxableIncome > slab.min) {
                val taxableInThisSlab = if (slab.max != null) {
                    (taxableIncome.coerceAtMost(slab.max) - slab.min)
                } else {
                    taxableIncome - slab.min
                }
                baseTax += taxableInThisSlab * slab.rate
            }
        }

        val cess = baseTax * 0.04
        val totalTax = baseTax + cess
        val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100 else 0.0

        return TaxResult(taxableIncome, baseTax, cess, totalTax, effectiveRate)
    }
}
