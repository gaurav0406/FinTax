package com.example.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MinimalPurplePrimary

@Composable
fun OnboardingScreen(onComplete: (String) -> Unit) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }
    
    var selectedProfile by remember { mutableStateOf("") }
    val profiles = listOf("Salaried Employee", "Retail Investor", "Self-Employed / Business", "Student / Beginner")
    
    val selectedInterests = remember { mutableStateListOf<String>() }
    val interests = listOf("Card Hacks & Perks", "Market Signals", "Tech & AI", "Startup & Capital", "Wealth 101")
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (currentStep == 1) "Tell us about yourself" else "What are you interested in?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentStep == 1) "Select your financial profile to personalize your feed." else "Select at least one topic to follow.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            if (currentStep == 1) {
                profiles.forEach { profile ->
                    val isSelected = selectedProfile == profile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MinimalPurplePrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedProfile = profile }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                interests.forEach { interest ->
                    val isSelected = selectedInterests.contains(interest)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MinimalPurplePrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                if (isSelected) selectedInterests.remove(interest)
                                else selectedInterests.add(interest)
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = interest,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    if (currentStep == 1 && selectedProfile.isNotEmpty()) {
                        currentStep = 2
                    } else if (currentStep == 2 && selectedInterests.isNotEmpty()) {
                        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("onboarding_complete", true).putString("initial_category", selectedInterests.first()).apply()
                        onComplete(selectedInterests.first())
                    }
                },
                enabled = if (currentStep == 1) selectedProfile.isNotEmpty() else selectedInterests.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MinimalPurplePrimary)
            ) {
                Text(text = if (currentStep == 1) "Next" else "Get Started", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
