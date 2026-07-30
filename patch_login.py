import sys

with open("app/src/main/java/com/example/ui/components/LoginScreen.kt", "r") as f:
    content = f.read()

content = content.replace("val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray", 
                          "val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)")

with open("app/src/main/java/com/example/ui/components/LoginScreen.kt", "w") as f:
    f.write(content)
