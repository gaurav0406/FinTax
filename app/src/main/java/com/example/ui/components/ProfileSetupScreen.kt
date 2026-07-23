package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.ui.NewsViewModel
import com.example.ui.theme.MinimalBackground
import com.example.ui.theme.MinimalPurpleDark
import com.example.ui.theme.MinimalPurpleLightContainer
import com.example.ui.theme.MinimalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val ALL_INTEREST_CATEGORIES = listOf(
    "Credit Cards",
    "ITR & Tax",
    "Loans & FDs",
    "Markets & Mutual Funds",
    "RBI & Policy",
    "Sports",
    "Cars & EV",
    "Education"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfileState by viewModel.userProfile.collectAsState()
    val profile = currentProfileState ?: UserProfileEntity()

    var isLoggedIn by remember(profile) { mutableStateOf(profile.isLoggedIn) }
    var name by remember(profile) { mutableStateOf(profile.userName) }
    var email by remember(profile) { mutableStateOf(profile.userEmail) }
    var ageText by remember(profile) { mutableStateOf(if (profile.age > 0) profile.age.toString() else "28") }
    var city by remember(profile) { mutableStateOf(profile.city) }
    var phone by remember(profile) { mutableStateOf(profile.mobileNumber) }

    val initialCategories = remember(profile) {
        profile.selectedCategories.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }
    var selectedCategories by remember(profile) { mutableStateOf(initialCategories) }

    var autoPlayAudio by remember(profile) { mutableStateOf(profile.autoPlayAudio) }
    var isSavedNotice by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Google Sign In Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MinimalPurpleLightContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (name.isNotBlank()) name.take(1).uppercase() else "G",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MinimalPurpleDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isLoggedIn) "Google Account Connected" else "Sign in with Google",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (isLoggedIn) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isLoggedIn = !isLoggedIn
                        if (isLoggedIn && name.isBlank()) {
                            name = "Gaurav Sharma"
                            email = "gs.gaurav0406@gmail.com"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoggedIn) Color(0xFFE0E0E0) else MinimalPurpleDark,
                        contentColor = if (isLoggedIn) TextPrimary else Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_login_toggle_button")
                ) {
                    Text(
                        text = if (isLoggedIn) "Disconnect Google Account" else "G  Sign In with Google",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal Details Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPurpleDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinimalPurpleDark
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City / Location") },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MinimalPurpleDark
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (Optional)") },
                    placeholder = { Text("+91 98765 43210") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPurpleDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Multi-select Interest Categories Tab setup
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Your Interest Categories",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Choose multiple topics to customize your daily quick updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ALL_INTEREST_CATEGORIES.forEach { category ->
                        val isSelected = selectedCategories.contains(category)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    selectedCategories = if (isSelected) {
                                        if (selectedCategories.size > 1) selectedCategories - category else selectedCategories
                                    } else {
                                        selectedCategories + category
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MinimalPurpleDark else Color.LightGray,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            color = if (isSelected) MinimalPurpleLightContainer else Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MinimalPurpleDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = category,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MinimalPurpleDark else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Settings Tab setup
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-play News Audio",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Automatically start audio playback when expanding a news card.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = autoPlayAudio,
                        onCheckedChange = { autoPlayAudio = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = MinimalPurpleDark)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Preferences Button
        Button(
            onClick = {
                val ageVal = ageText.toIntOrNull() ?: 28
                val categoriesStr = selectedCategories.joinToString(",")
                viewModel.saveUserProfile(
                    UserProfileEntity(
                        id = 1,
                        isLoggedIn = isLoggedIn,
                        userName = name,
                        userEmail = email,
                        age = ageVal,
                        city = city,
                        mobileNumber = phone,
                        selectedCategories = categoriesStr,
                        autoPlayAudio = autoPlayAudio
                    )
                )
                isSavedNotice = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = MinimalPurpleDark),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_profile_button")
        ) {
            Text(
                text = "Save Profile & Preferences",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        if (isSavedNotice) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Profile & Feed Preferences Saved!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        var showPrivacyPolicy by remember { mutableStateOf(false) }

        androidx.compose.material3.TextButton(
            onClick = { showPrivacyPolicy = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Privacy Policy", color = TextSecondary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        }

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.material3.OutlinedButton(
            onClick = {
                viewModel.saveUserProfile(profile.copy(isLoggedIn = false, hasLoggedOut = true))
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
        ) {
            Text("Logout", fontWeight = FontWeight.Bold)
        }

        if (showPrivacyPolicy) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showPrivacyPolicy = false },
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = "Last updated: July 22, 2026\n\n1. Introduction\nWelcome to FinTax Inshorts. We are committed to protecting your personal information and your right to privacy.\n\n2. Information We Collect\nWe may collect personal information such as your name, email, and preferences that you voluntarily provide when you register on the App.\n\n3. How We Use Your Information\nWe use personal information collected to facilitate account creation, personalize feed content, and to manage user accounts.\n\n4. Data Sharing\nWe do not share, sell, rent, or trade your information with third parties for their promotional purposes.\n\n5. Contact Us\nIf you have questions or comments about this policy, you may contact our privacy team.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showPrivacyPolicy = false }) {
                        Text("Close")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
