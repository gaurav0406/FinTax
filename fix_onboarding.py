import re

with open("app/src/main/java/com/example/ui/components/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Fix signature
content = content.replace("fun OnboardingScreen(\n    onComplete: (String, Int, String, String, List<String>) -> Unit\n) {", "fun OnboardingScreen(\n    initialName: String = \"\",\n    initialCity: String = \"\",\n    onComplete: (String, Int, String, String, List<String>) -> Unit\n) {")

with open("app/src/main/java/com/example/ui/components/OnboardingScreen.kt", "w") as f:
    f.write(content)
