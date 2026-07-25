import re

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'r') as f:
    lines = f.readlines()

for i in range(len(lines)):
    if 'contentColor = Color.White' in lines[i]:
        continue
    if 'selectedLabelColor = Color.White' in lines[i]:
        lines[i] = lines[i].replace('Color.White', 'MaterialTheme.colorScheme.onBackground')
    else:
        lines[i] = lines[i].replace('Color.White', 'MaterialTheme.colorScheme.onBackground')

with open('app/src/main/java/com/example/ui/components/InshortsFeedView.kt', 'w') as f:
    f.writelines(lines)
