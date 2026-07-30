import sys

with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

target = """                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoggedIn) Color(0xFFE0E0E0) else MinimalPurpleDark,
                        contentColor = if (isLoggedIn) MaterialTheme.colorScheme.onSurface else Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_login_toggle_button")
                ) {
                    Text(
                        text = if (isLoggedIn) "Disconnect Google Account" else "G  Sign In with Google",
                        fontWeight = FontWeight.Bold
                    )
                }"""

replacement = """                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoggedIn) MaterialTheme.colorScheme.errorContainer else MinimalPurpleDark,
                        contentColor = if (isLoggedIn) MaterialTheme.colorScheme.onErrorContainer else Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_login_toggle_button")
                ) {
                    Text(
                        text = if (isLoggedIn) "Disconnect Google Account" else "G  Sign In with Google",
                        fontWeight = FontWeight.Bold,
                        color = if (isLoggedIn) MaterialTheme.colorScheme.onErrorContainer else Color.White
                    )
                }"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/ProfileSetupScreen.kt", "w") as f:
        f.write(content)
    print("Successfully patched Disconnect button font visibility")
else:
    print("Target not found")
