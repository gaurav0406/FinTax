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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

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
import com.example.ui.theme.MinimalSecondaryContainer
import java.text.NumberFormat
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.ui.viewmodels.TaxCalculatorViewModel
import com.example.ui.viewmodels.TaxCalculatorState



@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaxCalculatorTab(
    modifier: Modifier = Modifier,
    viewModel: TaxCalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    val oldTaxResult = viewModel.calculateOldRegimeTax()
    val newTaxResult = viewModel.calculateNewRegimeTax()
    
    val taxSavings = kotlin.math.abs(oldTaxResult.totalTaxPayable - newTaxResult.totalTaxPayable)
    val recommendedRegime = if (newTaxResult.totalTaxPayable <= oldTaxResult.totalTaxPayable) "New Regime" else "Old Regime"
    
    var expandedFY by remember { mutableStateOf(false) }
    val fyOptions = listOf("FY 2025-26", "FY 2026-27 (Proposed)")

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
                        text = "Income tax calculator",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MinimalPurpleDark
                        )
                    )
                    Text(
                        text = "${state.currentPolicy.financialYear} Budget Slabs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("700000" to "₹7L", "1000000" to "₹10L", "1500000" to "₹15L").forEach { (amount, label) ->
                val isSelected = state.grossIncomeInput == amount
                Surface(
                    onClick = { viewModel.updateGrossIncome(amount) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MinimalPurplePrimary else MaterialTheme.colorScheme.surface,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MinimalBorder) else null,
                    modifier = Modifier.weight(1f).height(40.dp).testTag("preset_salary_$label")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
            
            // Manual entry field
            OutlinedTextField(
                value = state.grossIncomeInput,
                onValueChange = { newValue -> viewModel.updateGrossIncome(newValue.filter { it.isDigit() }) },
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
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MinimalPurpleDark,
                    unfocusedTextColor = MinimalPurpleDark
                ),
                
                modifier = Modifier.weight(1.2f).height(40.dp)
            )
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
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
                            .background(MaterialTheme.colorScheme.outlineVariant)
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

        // Financial Year Selection
        ExposedDropdownMenuBox(
            expanded = expandedFY,
            onExpandedChange = { expandedFY = !expandedFY }
        ) {
            OutlinedTextField(
                value = state.currentPolicy.financialYear,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Financial Year") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFY) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MinimalPurplePrimary,
                    unfocusedBorderColor = MinimalBorder,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedFY,
                onDismissRequest = { expandedFY = false }
            ) {
                fyOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            viewModel.selectFinancialYear(selectionOption)
                            expandedFY = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // Gross Salary Input
        OutlinedTextField(
            value = state.grossIncomeInput,
            onValueChange = { newValue -> viewModel.updateGrossIncome(newValue.filter { it.isDigit() }) },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 80C Deductions
        OutlinedTextField(
            value = state.sec80CInput,
            onValueChange = { viewModel.updateSec80C(it.filter { char -> char.isDigit() }) },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 80D Health Insurance
        OutlinedTextField(
            value = state.sec80DInput,
            onValueChange = { viewModel.updateSec80D(it.filter { char -> char.isDigit() }) },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // NPS 80CCD(1B)
        OutlinedTextField(
            value = state.npsInput,
            onValueChange = { viewModel.updateNps(it.filter { char -> char.isDigit() }) },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // HRA / Home Loan
        OutlinedTextField(
            value = state.hraLoanInput,
            onValueChange = { viewModel.updateHraLoan(it.filter { char -> char.isDigit() }) },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(120.dp))
    }
}



