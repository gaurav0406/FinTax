import sys

def patch_file(file_path):
    with open(file_path, "r") as f:
        content = f.read()

    # Cards border
    content = content.replace("Color.Gray.copy(alpha = 0.3f)", "Color.Gray.copy(alpha = 0.4f)")
    
    # Selection borders
    content = content.replace("color = if (isSelected) MinimalPurpleDark else Color.Gray.copy(alpha = 0.5f)",
                              "color = if (isSelected) MinimalPurplePrimary else Color.Gray.copy(alpha = 0.5f)")

    with open(file_path, "w") as f:
        f.write(content)

patch_file("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt")
patch_file("app/src/main/java/com/example/ui/components/OnboardingScreen.kt")
