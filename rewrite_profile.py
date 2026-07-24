import re

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

imports_to_add = """
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
"""

# add imports before import androidx.compose.ui.Modifier
content = content.replace("import androidx.compose.ui.Modifier", imports_to_add + "import androidx.compose.ui.Modifier")

setup_vars = """
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    
"""

content = content.replace("    var isLoggedIn by remember(profile) { mutableStateOf(profile.isLoggedIn) }", setup_vars + "    var isLoggedIn by remember(profile) { mutableStateOf(profile.isLoggedIn) }")

old_button_click = """                    onClick = {
                        isLoggedIn = !isLoggedIn
                        if (isLoggedIn && name.isBlank()) {
                            name = "Gaurav Sharma"
                            email = "gs.gaurav0406@gmail.com"
                        }
                    },"""

new_button_click = """                    onClick = {
                        if (isLoggedIn) {
                            auth.signOut()
                            isLoggedIn = false
                            name = ""
                            email = ""
                        } else {
                            coroutineScope.launch {
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(context.getString(com.example.R.string.default_web_client_id))
                                        .setAutoSelectEnabled(true)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(context, request)
                                    val credential = result.credential
                                    
                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                                        auth.signInWithCredential(firebaseCredential)
                                        val user = auth.currentUser
                                        if (user != null) {
                                            name = user.displayName ?: ""
                                            email = user.email ?: ""
                                            isLoggedIn = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },"""

content = content.replace(old_button_click, new_button_click)

# remove comments
comments_to_remove = """                // NOTE FOR PRODUCTION:
                // The ProfileSetupScreen currently uses mock SharedPreferences logic. 
                // For a production forum/community, you need real Firebase Authentication (Google Sign-in/Email).
                // 1. Add `implementation(platform(libs.firebase.bom))` and `implementation(libs.firebase.auth)` to build.gradle.kts.
                // 2. Obtain google-services.json from Firebase console and place it in the app/ directory.
                // 3. Integrate Google Sign-in API or FirebaseUI for actual authentication flow.
"""

content = content.replace(comments_to_remove, "")

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
