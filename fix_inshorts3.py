with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

bad_str = """        // Open In-App WebView Dialog if URL selected
                    )
        }
    }
}"""
content = content.replace(bad_str, "    }\n}")
with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)
