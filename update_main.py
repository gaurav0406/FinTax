import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

new_imports = """
import android.content.Context
import androidx.compose.runtime.*
import com.example.ui.OnboardingScreen
"""

if "import com.example.ui.OnboardingScreen" not in content:
    content = content.replace("import com.example.utils.AdMobHelper", "import com.example.utils.AdMobHelper\n" + new_imports.strip())

set_content_replacement = """
        setContent {
            FinTaxTheme {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                var isOnboardingComplete by remember { mutableStateOf(prefs.getBoolean("onboarding_complete", false)) }
                val newsViewModel: NewsViewModel = viewModel()
                
                LaunchedEffect(isOnboardingComplete) {
                    if (isOnboardingComplete) {
                        val initialCategory = prefs.getString("initial_category", "All") ?: "All"
                        newsViewModel.setCategory(initialCategory)
                    }
                }
                
                if (isOnboardingComplete) {
                    MainHomeScreen(viewModel = newsViewModel)
                } else {
                    OnboardingScreen(onComplete = { category -> 
                        isOnboardingComplete = true
                    })
                }
            }
        }
"""

content = re.sub(r'setContent \{.*?val newsViewModel: NewsViewModel = viewModel\(\)\s*MainHomeScreen\(viewModel = newsViewModel\)\s*\}\s*\}', set_content_replacement.strip(), content, flags=re.DOTALL)

if "import androidx.compose.ui.platform.LocalContext" not in content:
    content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.compose.ui.platform.LocalContext")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

