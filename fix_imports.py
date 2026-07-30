for file_path in ["app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "app/src/main/java/com/example/ui/components/NewsItemCard.kt"]:
    with open(file_path, "r") as f:
        content = f.read()
    
    if "import com.example.data.stripIntroductoryLabels" not in content:
        content = "import com.example.data.stripIntroductoryLabels\n" + content
        with open(file_path, "w") as f:
            f.write(content)
    print(f"Added import to {file_path}")

