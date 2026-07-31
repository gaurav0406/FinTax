package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.example.data.DatabaseCleanupWorker
import com.example.data.NewsSyncWorker
import com.example.notifications.RetentionNotificationScheduler
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
        
        // Request Notification Permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // Initialize and Schedule Retention Notifications
        RetentionNotificationScheduler.scheduleDailyNotifications(this)
        
        // Schedule 45-Minute Live News Fetch / Scraper Sync
        val syncRequest = PeriodicWorkRequestBuilder<NewsSyncWorker>(45, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NewsSync45Min",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

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

