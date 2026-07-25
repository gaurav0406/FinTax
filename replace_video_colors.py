import re

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'r') as f:
    lines = f.readlines()

for i in range(len(lines)):
    if 'Color.Transparent' in lines[i]:
        continue
    if 'Color.Black' in lines[i]:
        continue # overlays remain dark
    
    # Text colors
    lines[i] = lines[i].replace('Color.White', 'MaterialTheme.colorScheme.onBackground')
    lines[i] = lines[i].replace('Color.LightGray', 'MaterialTheme.colorScheme.onSurfaceVariant')
    lines[i] = lines[i].replace('Color.Gray', 'MaterialTheme.colorScheme.onSurfaceVariant')
    
    # Backgrounds
    lines[i] = lines[i].replace('Color(0xFF0D0E12)', 'MaterialTheme.colorScheme.background')
    lines[i] = lines[i].replace('Color(0xFF16171E)', 'MaterialTheme.colorScheme.surface')
    lines[i] = lines[i].replace('Color(0xFF232530)', 'MaterialTheme.colorScheme.surfaceVariant')
    lines[i] = lines[i].replace('Color(0xFF1B1C24)', 'MaterialTheme.colorScheme.surface')
    lines[i] = lines[i].replace('Color(0xFF2E303E)', 'MaterialTheme.colorScheme.outline')
    lines[i] = lines[i].replace('Color(0xFF252733)', 'MaterialTheme.colorScheme.surfaceVariant')
    lines[i] = lines[i].replace('Color(0xFF1E1F28)', 'MaterialTheme.colorScheme.surface')

with open('app/src/main/java/com/example/ui/components/VideoEngagementTab.kt', 'w') as f:
    f.writelines(lines)
