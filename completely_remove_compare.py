import re

with open("app/src/main/java/com/example/ui/components/AdaptiveArticleReaderScreen.kt", "r") as f:
    content = f.read()

# Let's remove the entire Surface block that contains "Perspectives" /* Disabled */
block_regex = re.compile(r'\s*Surface\(\s*shape = RoundedCornerShape\(12\.dp\),\s*color = MaterialTheme\.colorScheme\.surface,\s*border = BorderStroke\(1\.dp, MinimalBorder\),\s*modifier = Modifier\.fillMaxWidth\(\)\s*\)\s*\{\s*Column\(modifier = Modifier\.padding\(14\.dp\)\)\s*\{\s*Row\([\s\S]*?Text\("Perspectives" /\* Disabled \*/[\s\S]*?\}\s*\}\s*\}\s*\}\s*\}', re.MULTILINE)

content = block_regex.sub('', content)

# We will just manually search and replace the chunk printed in task 323.
# Since it is a bit complex, let's just leave the disabled button, but change the text to "View Related Articles" or something and disable the expansion, or just leave it.

