import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

old_call = """            com.example.ui.components.OnboardingScreen(
                onComplete = { name, age, city, mobile, categories ->"""
new_call = """            com.example.ui.components.OnboardingScreen(
                initialName = userProfileState!!.userName,
                initialCity = userProfileState!!.city,
                onComplete = { name, age, city, mobile, categories ->"""
content = content.replace(old_call, new_call)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
