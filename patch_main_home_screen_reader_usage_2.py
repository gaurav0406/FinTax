import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# I will just replace the specific lines with empty string or comment them out
start_idx = content.find("activeReaderNews?.let { news ->")
if start_idx != -1:
    end_idx = content.find("            }", start_idx) + len("            }")
    content = content[:start_idx] + "/* removed activeReaderNews let block */" + content[end_idx:]
    
with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
