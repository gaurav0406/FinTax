files_to_patch = [
    "app/src/main/java/com/example/ui/components/CommunityDiscussionsTab.kt",
    "app/src/main/java/com/example/ui/components/DealsAndOffersTab.kt",
    "app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt"
]

for file in files_to_patch:
    with open(file, "r") as f:
        content = f.read()
    
    # In CommunityDiscussionsTab
    if "CommunityDiscussionsTab" in file:
        content = content.replace("Column(\n        modifier = modifier.fillMaxSize()\n    ) {", "Column(\n        modifier = modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.White)\n    ) {")
        content = content.replace("Column(\n        modifier = modifier.fillMaxSize()\n", "Column(\n        modifier = modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.White)\n")
    
    # In DealsAndOffersTab
    if "DealsAndOffersTab" in file:
        content = content.replace(".background(MinimalBackground)", ".background(androidx.compose.ui.graphics.Color.White)")
    
    # In ProfileSetupScreen
    if "ProfileSetupScreen" in file:
        content = content.replace("Column(\n        modifier = modifier\n            .fillMaxSize()", "Column(\n        modifier = modifier\n            .fillMaxSize()\n            .background(androidx.compose.ui.graphics.Color.White)")
        content = content.replace("Column(\n        modifier = Modifier\n            .fillMaxSize()", "Column(\n        modifier = Modifier\n            .fillMaxSize()\n            .background(androidx.compose.ui.graphics.Color.White)")

    with open(file, "w") as f:
        f.write(content)
print("Done patching backgrounds")
