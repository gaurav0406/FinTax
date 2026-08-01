with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "r") as f:
    content = f.read()

bad_str = """        }
    }
            )
    }

    if (showJargonSheet) {"""

content = content.replace(bad_str, """        }
    }

    if (showJargonSheet) {""")

with open("app/src/main/java/com/example/ui/components/NewsItemCard.kt", "w") as f:
    f.write(content)
