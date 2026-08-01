import re

with open("app/src/main/java/com/example/ui/components/AdaptiveArticleReaderScreen.kt", "r") as f:
    content = f.read()

# Instead of regex, just find the text and cut out the surface
idx = content.find('Text("Perspectives" /* Disabled */')
if idx != -1:
    # find the preceding Surface
    surface_start = content.rfind('Surface(', 0, idx)
    if surface_start != -1:
        # We need to find the matching closing brace.
        brace_count = 0
        started = False
        surface_end = -1
        for i in range(surface_start, len(content)):
            if content[i] == '{':
                brace_count += 1
                started = True
            elif content[i] == '}':
                brace_count -= 1
                if started and brace_count == 0:
                    surface_end = i + 1
                    break
        
        if surface_end != -1:
            content = content[:surface_start] + content[surface_end:]
            
            with open("app/src/main/java/com/example/ui/components/AdaptiveArticleReaderScreen.kt", "w") as f:
                f.write(content)
