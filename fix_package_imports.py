for file_path in ["app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "app/src/main/java/com/example/ui/components/NewsItemCard.kt"]:
    with open(file_path, "r") as f:
        content = f.read()
    
    # Remove any import com.example.data.stripIntroductoryLabels before package
    content = content.replace("import com.example.data.stripIntroductoryLabels\npackage com.example.ui.components", "package com.example.ui.components")
    
    if "import com.example.data.stripIntroductoryLabels" not in content:
        content = content.replace("package com.example.ui.components\n", "package com.example.ui.components\n\nimport com.example.data.stripIntroductoryLabels\n")
        
    with open(file_path, "w") as f:
        f.write(content)
    print(f"Fixed package and import order in {file_path}")

