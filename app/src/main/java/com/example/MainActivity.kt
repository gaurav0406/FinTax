package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainHomeScreen
import com.example.ui.NewsViewModel
import com.example.ui.theme.FinTaxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinTaxTheme {
                val newsViewModel: NewsViewModel = viewModel()
                MainHomeScreen(viewModel = newsViewModel)
            }
        }
    }
}
