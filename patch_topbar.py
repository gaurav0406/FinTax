import sys

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "colors = TopAppBarDefaults.topAppBarColors(\n                    containerColor = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color(0xFF0D0E12) else MaterialTheme.colorScheme.background,",
    "colors = TopAppBarDefaults.topAppBarColors(\n                    containerColor = if ((useInshortsViewMode && activeTab == 0) || activeTab == 5) Color(0xFF0D0E12) else MaterialTheme.colorScheme.surface,"
)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

