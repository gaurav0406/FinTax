package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AiGeneratorTab(
    isGenerating: Boolean,
    statusMessage: String,
    onGenerate: (rawText: String, sourceUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var rawText by remember { mutableStateOf("") }
    var sourceUrl by remember { mutableStateOf("https://eportal.incometax.gov.in") }

    val sampleTextPrompt = """CBDT Notification 2026: The Central Board of Direct Taxes has updated filing requirements for Section 80C deductions and Section 87A rebate applicability for taxpayers choosing the New Tax Regime. Salaried individuals earning up to Rs 7.5 Lakhs can claim full rebate. Online verification through Aadhaar OTP is required before July 31."""

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("ai_generator_tab")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MinimalPurpleLightContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MinimalPurpleDark,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AI Financial NLP Engine",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MinimalPurpleDark
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Paste any raw Indian tax circular, RBI press release, or financial article to generate a 3-bullet structured summary and 60-word voice audio digest.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sample Fill Button
        Surface(
            onClick = { rawText = sampleTextPrompt },
            shape = RoundedCornerShape(50),
            color = MinimalPurpleLightContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚡ Load Sample CBDT Circular Text",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MinimalPurpleDark
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Raw Article Input Field
        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            label = { Text("Raw News / Tax Article Content") },
            placeholder = { Text("Paste text here...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag("ai_raw_text_input"),
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Source URL Input
        OutlinedTextField(
            value = sourceUrl,
            onValueChange = { sourceUrl = it },
            label = { Text("Original Source Article URL") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_source_url_input"),
            shape = RoundedCornerShape(20.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = { onGenerate(rawText, sourceUrl) },
            enabled = rawText.isNotBlank() && !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("ai_submit_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MinimalPurplePrimary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processing with AI...")
            } else {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Summarize & Generate 60-Sec Audio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (statusMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MinimalPurplePrimary
            )
        }
    }
}
