package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalBorder
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.MinimalSecondaryContainer
import java.text.NumberFormat
import java.util.Locale

data class TaxResult(
    val netTaxableIncome: Double,
    val baseTax: Double,
    val cess: Double,
    val totalTaxPayable: Double,
    val effectiveTaxRate: Double
)

@Composable
fun TaxCalculatorTab(
    modifier: Modifier = Modifier
) {
    var grossIncomeInput by remember { mutableStateOf("1200000") }
    var sec80CInput by remember { mutableStateOf("150000") }
    var sec80DInput by remember { mutableStateOf("25000") }
    var npsInput by remember { mutableStateOf("50000") }
    var hraLoanInput by remember { mutableStateOf("100000") }
    var isSalaried by remember { mutableStateOf(true) }

    val grossIncome = grossIncomeInput.toDoubleOrNull() ?: 0.0
    val sec80C = (sec80CInput.toDoubleOrNull() ?: 0.0).coerceAtMost(150000.0)
    val sec80D = (sec80DInput.toDoubleOrNull() ?: 0.0).coerceAtMost(75000.0)
    val nps = (npsInput.toDoubleOrNull() ?: 0.0).coerceAtMost(50000.0)
    val hraLoan = hraLoanInput.toDoubleOrNull() ?: 0.0

    val oldTaxResult by remember(grossIncome, sec80C, sec80D, nps, hraLoan, isSalaried) {
        derivedStateOf {
            calculateOldRegimeTax(grossIncome, sec80C, sec80D, nps, hraLoan, isSalaried)
        }
    }

    val newTaxResult by remember(grossIncome, isSalaried) {
        derivedStateOf {
            calculateNewRegimeTax(grossIncome, isSalaried)
        }
    }

    val taxSavings by remember(oldTaxResult, newTaxResult) {
        derivedStateOf {
            kotlin.math.abs(oldTaxResult.totalTaxPayable - newTaxResult.totalTaxPayable)
        }
    }

    val recommendedRegime by remember(oldTaxResult, newTaxResult) {
        derivedStateOf {
            if (newTaxResult.totalTaxPayable <= oldTaxResult.totalTaxPayable) "New Regime" else "Old Regime"
        }
    }

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("tax_calculator_tab")
    ) {
        // Top Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MinimalPurpleLightContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MinimalPurpleDark,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Income Tax Regime Calculator",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurpleDark
                        )
                    )
                    Text(
                        text = "FY 2024-25 & FY 2025-26 Budget Slabs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Preset Salary Buttons
        Text(
            text = "QUICK SALARY PRESETS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MinimalPurpleDark
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("700000" to "₹7 Lakhs", "1000000" to "₹10 Lakhs", "1500000" to "₹15 Lakhs", "2500000" to "₹25 Lakhs").forEach { (amount, label) ->
                val isSelected = grossIncomeInput == amount
                Surface(
                    onClick = { grossIncomeInput = amount },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MinimalPurplePrimary else Color.White,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MinimalBorder) else null,
                    modifier = Modifier.weight(1f).testTag("preset_salary_$label")
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MinimalPurpleDark
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comparison Result Recommendation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (recommendedRegime == "New Regime") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = if (recommendedRegime == "New Regime") Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RECOMMENDED: $recommendedRegime",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (recommendedRegime == "New Regime") Color(0xFF1B5E20) else Color(0xFFBF360C)
                            )
                        )
                        Text(
                            text = if (taxSavings > 0) "Saves you ${currencyFormatter.format(taxSavings)} in income tax!" else "Both regimes result in zero tax!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MinimalPurpleDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OLD REGIME TAX",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(oldTaxResult.totalTaxPayable),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (recommendedRegime == "Old Regime") MinimalPurplePrimary else Color.DarkGray
                            )
                        )
                        Text(
                            text = "Effective: ${String.format("%.1f", oldTaxResult.effectiveTaxRate)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(Color.Black.copy(alpha = 0.1f))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "NEW REGIME TAX",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(newTaxResult.totalTaxPayable),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (recommendedRegime == "New Regime") Color(0xFF2E7D32) else Color.DarkGray
                            )
                        )
                        Text(
                            text = "Effective: ${String.format("%.1f", newTaxResult.effectiveTaxRate)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Income & Deduction Input Fields
        Text(
            text = "INCOME & DEDUCTIONS BREAKDOWN",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MinimalPurpleDark
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gross Salary Input
        OutlinedTextField(
            value = grossIncomeInput,
            onValueChange = { grossIncomeInput = it.filter { char -> char.isDigit() } },
            label = { Text("Gross Annual Salary / Income (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gross_income_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MinimalPurplePrimary,
                unfocusedBorderColor = MinimalBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 80C Deductions
        OutlinedTextField(
            value = sec80CInput,
            onValueChange = { sec80CInput = it.filter { char -> char.isDigit() } },
            label = { Text("Section 80C (PPF, ELSS, EPF, LIC - Max ₹1.5L)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sec_80c_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MinimalPurplePrimary,
                unfocusedBorderColor = MinimalBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 80D Health Insurance
        OutlinedTextField(
            value = sec80DInput,
            onValueChange = { sec80DInput = it.filter { char -> char.isDigit() } },
            label = { Text("Section 80D (Health Insurance Premium - Max ₹75k)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sec_80d_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MinimalPurplePrimary,
                unfocusedBorderColor = MinimalBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // NPS 80CCD(1B)
        OutlinedTextField(
            value = npsInput,
            onValueChange = { npsInput = it.filter { char -> char.isDigit() } },
            label = { Text("Section 80CCD(1B) NPS Tier 1 (Max ₹50,000)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("nps_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MinimalPurplePrimary,
                unfocusedBorderColor = MinimalBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // HRA / Home Loan
        OutlinedTextField(
            value = hraLoanInput,
            onValueChange = { hraLoanInput = it.filter { char -> char.isDigit() } },
            label = { Text("HRA Exemption / Sec 24 Home Loan Interest") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hra_loan_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MinimalPurplePrimary,
                unfocusedBorderColor = MinimalBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Detailed Tax Slabs Info Note
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MinimalSecondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MinimalPurpleDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "New Tax Regime Key Highlights:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MinimalPurpleDark)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Standard Deduction of ₹75,000 available for salaried individuals.\n• Full Section 87A Tax Rebate for taxable income up to ₹7 Lakhs (no tax up to ₹7.75 Lakhs gross salary).\n• Revised Slabs: 0-3L (0%), 3-7L (5%), 7-10L (10%), 10-12L (15%), 12-15L (20%), >15L (30%).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private fun calculateOldRegimeTax(
    grossIncome: Double,
    sec80C: Double,
    sec80D: Double,
    nps: Double,
    hraLoan: Double,
    isSalaried: Boolean
): TaxResult {
    val stdDeduction = if (isSalaried) 50000.0 else 0.0
    val totalDeductions = stdDeduction + sec80C + sec80D + nps + hraLoan
    val taxableIncome = (grossIncome - totalDeductions).coerceAtLeast(0.0)

    var baseTax = 0.0
    if (taxableIncome <= 500000.0) {
        // Section 87A rebate makes tax zero if income <= 5L
        baseTax = 0.0
    } else {
        // Slabs
        // 0 to 2.5L -> 0%
        // 2.5L to 5L -> 5% (12,500)
        // 5L to 10L -> 20%
        // Above 10L -> 30%
        baseTax += 12500.0
        if (taxableIncome > 1000000.0) {
            baseTax += (500000.0 * 0.20)
            baseTax += ((taxableIncome - 1000000.0) * 0.30)
        } else {
            baseTax += ((taxableIncome - 500000.0) * 0.20)
        }
    }

    val cess = baseTax * 0.04
    val totalTax = baseTax + cess
    val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100 else 0.0

    return TaxResult(taxableIncome, baseTax, cess, totalTax, effectiveRate)
}

private fun calculateNewRegimeTax(
    grossIncome: Double,
    isSalaried: Boolean
): TaxResult {
    val stdDeduction = if (isSalaried) 75000.0 else 0.0
    val taxableIncome = (grossIncome - stdDeduction).coerceAtLeast(0.0)

    var baseTax = 0.0

    if (taxableIncome <= 700000.0) {
        // Full 87A Rebate for net income up to 7L
        baseTax = 0.0
    } else {
        // New Slabs FY 24-25 / 25-26
        // 0 - 3L: 0%
        // 3L - 7L: 5% = 20,000
        // 7L - 10L: 10% = 30,000
        // 10L - 12L: 15% = 30,000
        // 12L - 15L: 20% = 60,000
        // > 15L: 30%

        var remaining = taxableIncome

        // 0-3L
        remaining = (remaining - 300000.0).coerceAtLeast(0.0)

        // 3L-7L (400k range)
        val slab1 = remaining.coerceAtMost(400000.0)
        baseTax += slab1 * 0.05
        remaining = (remaining - 400000.0).coerceAtLeast(0.0)

        // 7L-10L (300k range)
        val slab2 = remaining.coerceAtMost(300000.0)
        baseTax += slab2 * 0.10
        remaining = (remaining - 300000.0).coerceAtLeast(0.0)

        // 10L-12L (200k range)
        val slab3 = remaining.coerceAtMost(200000.0)
        baseTax += slab3 * 0.15
        remaining = (remaining - 200000.0).coerceAtLeast(0.0)

        // 12L-15L (300k range)
        val slab4 = remaining.coerceAtMost(300000.0)
        baseTax += slab4 * 0.20
        remaining = (remaining - 300000.0).coerceAtLeast(0.0)

        // > 15L
        if (remaining > 0) {
            baseTax += remaining * 0.30
        }

        // Marginal Relief check for taxable income slightly above 7L
        val incomeExcessOver7L = taxableIncome - 700000.0
        if (baseTax > incomeExcessOver7L) {
            baseTax = incomeExcessOver7L
        }
    }

    val cess = baseTax * 0.04
    val totalTax = baseTax + cess
    val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100 else 0.0

    return TaxResult(taxableIncome, baseTax, cess, totalTax, effectiveRate)
}
