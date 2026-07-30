import sys
import glob

files = glob.glob("app/src/main/java/com/example/ui/**/*.kt", recursive=True)

for file in files:
    with open(file, "r") as f:
        lines = f.readlines()
    
    with open(file, "w") as f:
        for line in lines:
            if "import com.example.ui.theme.MaterialTheme" in line:
                continue
            if "import MaterialTheme.colorScheme" in line:
                continue
            f.write(line)
