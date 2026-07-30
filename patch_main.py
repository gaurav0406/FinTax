import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

target = """    if (userProfileState != null) {
        if (!userProfileState!!.isLoggedIn) {
            if (userProfileState!!.hasLoggedOut) {
                com.example.ui.components.LogoutScreen(
                    newsList = allNewsList,
                    onLoginAgain = {
                        viewModel.saveUserProfile(userProfileState!!.copy(hasLoggedOut = false))
                    }
                )
            } else {
                com.example.ui.components.LoginScreen(
                    onLoginSuccess = { name, city ->
                        val updatedProfile = userProfileState!!.copy(
                            isLoggedIn = true, 
                            hasLoggedOut = false,
                            userName = name ?: userProfileState!!.userName,
                            city = city ?: userProfileState!!.city
                        )
                        viewModel.saveUserProfile(updatedProfile)
                    }
                )
            }
            return
        }
        
        if (!userProfileState!!.isOnboarded) {
            com.example.ui.components.OnboardingScreen(
                initialName = userProfileState!!.userName,
                initialCity = userProfileState!!.city,
                onComplete = { name, age, city, mobile, categories ->
                    val profile = userProfileState!!.copy(
                        isOnboarded = true,
                        userName = name,
                        age = age,
                        city = city,
                        mobileNumber = mobile,
                        selectedCategories = categories.joinToString(",")
                    )
                    viewModel.saveUserProfile(profile)
                }
            )
            return
        }"""

replacement = """    if (userProfileState != null) {
        if (!userProfileState!!.isOnboarded && !userProfileState!!.hasLoggedOut) {
            com.example.ui.components.OnboardingScreen(
                initialName = userProfileState!!.userName,
                initialCity = userProfileState!!.city,
                onComplete = { name, age, city, mobile, categories, jobProfile ->
                    val profile = userProfileState!!.copy(
                        isLoggedIn = true,
                        isOnboarded = true,
                        hasLoggedOut = false,
                        userName = name,
                        age = age,
                        city = city,
                        mobileNumber = mobile,
                        selectedCategories = categories.joinToString(","),
                        jobProfile = jobProfile
                    )
                    viewModel.saveUserProfile(profile)
                }
            )
            return
        }

        if (!userProfileState!!.isLoggedIn) {
            if (userProfileState!!.hasLoggedOut) {
                com.example.ui.components.LogoutScreen(
                    newsList = allNewsList,
                    onLoginAgain = {
                        viewModel.saveUserProfile(userProfileState!!.copy(hasLoggedOut = false))
                    }
                )
            } else {
                com.example.ui.components.LoginScreen(
                    onLoginSuccess = { name, city ->
                        val updatedProfile = userProfileState!!.copy(
                            isLoggedIn = true, 
                            hasLoggedOut = false,
                            userName = name ?: userProfileState!!.userName,
                            city = city ?: userProfileState!!.city
                        )
                        viewModel.saveUserProfile(updatedProfile)
                    }
                )
            }
            return
        }"""

new_content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(new_content)
