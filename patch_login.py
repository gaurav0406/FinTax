import re

with open("app/src/main/java/com/example/ui/components/LoginScreen.kt", "r") as f:
    content = f.read()

# Modify LoginScreen signature to return name and city
content = content.replace("fun LoginScreen(\n    onLoginSuccess: () -> Unit\n)", "fun LoginScreen(\n    onLoginSuccess: (name: String?, city: String?) -> Unit\n)")

# Find "Continue with Google" click and update it
google_button = """                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(1000)
                            isLoading = false
                            onLoginSuccess()
                        }
                    },"""
google_button_new = """                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(1000)
                            isLoading = false
                            onLoginSuccess("Gaurav Sharma", "Mumbai") // Mocked Google Data
                        }
                    },"""
content = content.replace(google_button, google_button_new)

# Update PhoneLogin
phone_button = """            PhoneLogin(
                onLoginClick = {
                    scope.launch {
                        isLoading = true
                        delay(1000) // Simulate OTP verify
                        isLoading = false
                        onLoginSuccess()
                    }
                },"""
phone_button_new = """            PhoneLogin(
                onLoginClick = {
                    scope.launch {
                        isLoading = true
                        delay(1000) // Simulate OTP verify
                        isLoading = false
                        onLoginSuccess(null, null)
                    }
                },"""
content = content.replace(phone_button, phone_button_new)

with open("app/src/main/java/com/example/ui/components/LoginScreen.kt", "w") as f:
    f.write(content)
