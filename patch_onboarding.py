import re

with open("app/src/main/java/com/example/ui/components/OnboardingScreen.kt", "r") as f:
    content = f.read()

# signature
old_sig = "fun OnboardingScreen(onComplete: (String, Int, String, String, List<String>) -> Unit)"
new_sig = "fun OnboardingScreen(initialName: String = \"\", initialCity: String = \"\", onComplete: (String, Int, String, String, List<String>) -> Unit)"
content = content.replace(old_sig, new_sig)

# remember states
old_name = "var name by remember { mutableStateOf(\"\") }"
new_name = "var name by remember { mutableStateOf(initialName) }"
content = content.replace(old_name, new_name)

old_city = "var city by remember { mutableStateOf(\"\") }"
new_city = "var city by remember { mutableStateOf(initialCity) }"
content = content.replace(old_city, new_city)

with open("app/src/main/java/com/example/ui/components/OnboardingScreen.kt", "w") as f:
    f.write(content)
