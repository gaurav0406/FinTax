import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

old_login_call = """                com.example.ui.components.LoginScreen(
                    onLoginSuccess = {
                        viewModel.saveUserProfile(userProfileState!!.copy(isLoggedIn = true, hasLoggedOut = false))
                    }
                )"""
new_login_call = """                com.example.ui.components.LoginScreen(
                    onLoginSuccess = { name, city ->
                        val updatedProfile = userProfileState!!.copy(
                            isLoggedIn = true, 
                            hasLoggedOut = false,
                            userName = name ?: userProfileState!!.userName,
                            city = city ?: userProfileState!!.city
                        )
                        viewModel.saveUserProfile(updatedProfile)
                    }
                )"""
content = content.replace(old_login_call, new_login_call)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
