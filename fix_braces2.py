with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    lines = f.readlines()

# find line with: "            } // End of PullToRefreshBox" equivalent
# actually let's just trace the braces.
