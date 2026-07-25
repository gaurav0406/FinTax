import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# Just replace `selectedIconColor = MinimalPurpleLightContainer,` 
# with `selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = MinimalPurplePrimary,`

content = content.replace("selectedIconColor = MinimalPurpleLightContainer,", "selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = MinimalPurplePrimary,")
# The first one might need Color.White but MinimalPurplePrimary is also fine. Let's just fix the rest.
content = content.replace("selectedTextColor = MinimalPurplePrimary,\n                            indicatorColor = MinimalPurplePrimary,\n                            unselectedIconColor = if (useInshortsViewMode && activeTab == 0)", 
"selectedTextColor = if (useInshortsViewMode && activeTab == 0) Color.White else MinimalPurplePrimary,\n                            indicatorColor = MinimalPurplePrimary,\n                            unselectedIconColor = if (useInshortsViewMode && activeTab == 0)")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

