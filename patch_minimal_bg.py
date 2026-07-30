import sys

def patch_file(file_path):
    with open(file_path, "r") as f:
        content = f.read()

    content = content.replace("MinimalBackground", "MaterialTheme.colorScheme.background")

    with open(file_path, "w") as f:
        f.write(content)

patch_file("app/src/main/java/com/example/ui/MainHomeScreen.kt")
patch_file("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt")
