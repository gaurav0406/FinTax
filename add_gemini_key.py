import re

with open('app/build.gradle.kts', 'r') as f:
    text = f.read()

# Add buildConfigField if not there
if "buildConfigField(\"String\", \"GEMINI_API_KEY\"" not in text:
    pattern = r'buildConfigField\("String", "SUPABASE_KEY", "(.*?)"\)'
    text = re.sub(pattern, r'buildConfigField("String", "SUPABASE_KEY", "\1")\n        buildConfigField("String", "GEMINI_API_KEY", "\\\"\\\"")', text)

with open('app/build.gradle.kts', 'w') as f:
    f.write(text)
