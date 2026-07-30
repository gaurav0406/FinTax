package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.NewsRepository
import com.example.ui.MainHomeScreen
import com.example.ui.NewsViewModel
import com.example.ui.theme.FinTaxTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class MainHomeScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var viewModel: NewsViewModel

    @Before
    fun setup() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        if (com.google.firebase.FirebaseApp.getApps(application).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(application)
        }
        val db = AppDatabase.getDatabase(application)
        val dao = db.financialNewsDao()
        kotlinx.coroutines.runBlocking {
            dao.saveUserProfile(com.example.data.UserProfileEntity(isLoggedIn = true, isOnboarded = true))
        }
        viewModel = NewsViewModel(application)
    }

    @Test
    fun testNavigationTabs() {
        composeTestRule.setContent {
            FinTaxTheme {
                MainHomeScreen(viewModel = viewModel)
            }
        }

        composeTestRule.waitForIdle()
        
        // Check community tab
        composeTestRule.onNodeWithTag("nav_tab_community").performClick()
        composeTestRule.waitForIdle()

        // Check tax calc tab
        composeTestRule.onNodeWithTag("nav_tab_tax_calc").performClick()
        composeTestRule.waitForIdle()
        
        // Check deals tab
        composeTestRule.onNodeWithTag("nav_tab_deals").performClick()
        composeTestRule.waitForIdle()

        // Check profile tab (open drawer first)
        composeTestRule.onNodeWithTag("top_bar_back_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("top_bar_menu_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("nav_tab_profile").performClick()
        composeTestRule.waitForIdle()
    }
}
