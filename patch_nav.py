import re

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

# Add Profile to drawer menu
drawer_addition = """                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = activeTab == 6,
                    onClick = { 
                        viewModel.setActiveTab(6)
                        scope.launch { drawerState.close() }
                    }
                )
"""
content = content.replace("                NavigationDrawerItem(\n                    icon = { Icon(Icons.Default.Settings", drawer_addition + "                NavigationDrawerItem(\n                    icon = { Icon(Icons.Default.Settings")

# Change NavigationBarItem for Profile (index 5) to Videos
old_nav_item = """                    NavigationBarItem(
                        selected = activeTab == 5,
                        onClick = { viewModel.setActiveTab(5) },
                        icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )"""

new_nav_item = """                    NavigationBarItem(
                        selected = activeTab == 5,
                        onClick = { viewModel.setActiveTab(5) },
                        icon = { Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Videos") },
                        label = { Text("Videos", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1, softWrap = false) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalPurpleLightContainer, selectedTextColor = MinimalPurplePrimary,
                            indicatorColor = MinimalPurplePrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_tab_videos")
                    )"""

content = content.replace(old_nav_item, new_nav_item)

# Add VideoEngagementTab import
content = content.replace("import com.example.ui.components.DealsAndOffersTab", "import com.example.ui.components.DealsAndOffersTab\nimport com.example.ui.components.VideoEngagementTab\nimport androidx.compose.material.icons.filled.PlayCircle")

# Add VideoEngagementTab to when (activeTab)
old_when = """                4 -> DealsAndOffersTab()
                5 -> ProfileSetupScreen(viewModel = viewModel)"""

new_when = """                4 -> DealsAndOffersTab()
                5 -> VideoEngagementTab()
                6 -> ProfileSetupScreen(viewModel = viewModel)"""

content = content.replace(old_when, new_when)

# Make "Saved" tab use InshortsFeedView to enforce one article per page
old_saved_tab = """                2 -> StandardCardListView(
                    newsList = bookmarkedList,
                    categories = emptyList(),
                    selectedCategory = "All",
                    searchQuery = "",
                    playingNewsId = playbackState.activeNewsId,
                    isPlaying = playbackState.isPlaying,
                    onSelectCategory = {},
                    onSearchQueryChange = {},
                    onPlayAudio = { viewModel.playAudio(it) },
                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                    onOpenComments = { news -> selectedNewsForComments = news },
                    autoPlayAudio = userProfileState?.autoPlayAudio == true,
                    emptyMessage = "No saved articles yet! Tap the bookmark icon on any news card to save for offline reading."
                )"""

new_saved_tab = """                2 -> InshortsFeedView(
                    newsList = bookmarkedList,
                    categories = emptyList(),
                    selectedCategory = "All",
                    playingNewsId = playbackState.activeNewsId,
                    isPlaying = playbackState.isPlaying,
                    onSelectCategory = {},
                    onPlayAudio = { viewModel.playAudio(it) },
                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                    onOpenComments = { news -> selectedNewsForComments = news }
                )"""
content = content.replace(old_saved_tab, new_saved_tab)

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)
