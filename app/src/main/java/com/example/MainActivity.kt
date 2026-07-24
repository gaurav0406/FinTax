package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainHomeScreen
import com.example.ui.NewsViewModel
import com.example.ui.theme.FinTaxTheme
import com.example.utils.AdMobHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AdMob and start preloading Interstitial Ad
        AdMobHelper.initialize(this)
        
        enableEdgeToEdge()
        setContent {
            FinTaxTheme {
                val newsViewModel: NewsViewModel = viewModel()
                MainHomeScreen(viewModel = newsViewModel)
            }
        }
    }
}
