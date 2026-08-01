import re

def update_file(filename):
    with open(filename, "r") as f:
        content = f.read()

    content = content.replace('"Entertainment"', '"Entertainment", "Gaming"')
    content = content.replace('"Technology"', '"Technology", "Gaming"')

    with open(filename, "w") as f:
        f.write(content)

update_file("app/src/main/java/com/example/ui/MainHomeScreen.kt")
update_file("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt")
update_file("app/src/main/java/com/example/ui/components/OnboardingScreen.kt")
