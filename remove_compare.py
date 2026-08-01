import re

with open("app/src/main/java/com/example/ui/components/AdaptiveArticleReaderScreen.kt", "r") as f:
    content = f.read()

# We look for the drawer code and remove it
drawer_start = content.find('// Side-by-Side Perspectives Drawer ("Compare Media Coverage")')
if drawer_start != -1:
    # Find the end of the drawer block. We know it ends around '    } // End of ModalNavigationDrawer'
    drawer_end = content.find('} // End of ModalNavigationDrawer', drawer_start)
    if drawer_end != -1:
        # Instead of parsing brackets, we will replace the whole drawer call.
        # Actually, ModalNavigationDrawer wraps the Scaffold. 
        # So removing the drawer means replacing ModalNavigationDrawer with just its content, or simply hiding the drawer content.
        pass

# A safer approach is to replace the button that opens the drawer or the text
content = content.replace('Text("Compare Media Perspectives"', 'Text("Perspectives" /* Disabled */')
# Or completely hide the FAB/button that opens it.
# Let's just find and replace the FAB or button that toggles `perspectivesDrawerState`

with open("app/src/main/java/com/example/ui/components/AdaptiveArticleReaderScreen.kt", "w") as f:
    f.write(content)
