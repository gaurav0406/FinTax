import sys

with open("app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt", "r") as f:
    content = f.read()

target = """            }
        }
    }
}"""

replacement = """            }
        }
        
        Spacer(modifier = Modifier.height(120.dp))
    }
}"""

if target in content:
    content = content.replace(target, replacement)
    with open("app/src/main/java/com/example/ui/components/TaxCalculatorTab.kt", "w") as f:
        f.write(content)
    print("Success Tax")
else:
    print("Not found Tax")
