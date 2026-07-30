package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.example.data.DatabaseCleanupWorker
import com.example.ui.MainHomeScreen
import com.example.ui.NewsViewModel
import com.example.ui.theme.FinTaxTheme
import com.example.utils.AdMobHelper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AdMob and start preloading Interstitial Ad
        AdMobHelper.initialize(this)
        
        // Schedule DB Cleanup
        val workRequest = PeriodicWorkRequestBuilder<DatabaseCleanupWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DatabaseCleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        
        enableEdgeToEdge()
        setContent {
            val newsViewModel: NewsViewModel = viewModel()
            val isDarkThemeState by newsViewModel.isDarkTheme.collectAsState()
            val systemTheme = isSystemInDarkTheme()
            val darkTheme = isDarkThemeState ?: systemTheme
            
            FinTaxTheme(darkTheme = darkTheme) {
                MainHomeScreen(viewModel = newsViewModel)
            }
        }
    }
}

