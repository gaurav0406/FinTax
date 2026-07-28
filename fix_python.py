import re

with open("app/src/main/java/com/example/ui/components/PythonPipelineTab.kt", "r") as f:
    content = f.read()

content = content.replace('model = genai.GenerativeModel("gemini-1.5-flash")', 'model = genai.GenerativeModel("gemini-2.0-flash")')
content = content.replace('model = genai.GenerativeModel("gemini-2.5-flash")', 'model = genai.GenerativeModel("gemini-2.0-flash")')

with open("app/src/main/java/com/example/ui/components/PythonPipelineTab.kt", "w") as f:
    f.write(content)
