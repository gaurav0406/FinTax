with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("newsList = filteredNewsList,", "allNewsList = filteredNewsList,")
content = content.replace("newsList = bookmarkedList,", "allNewsList = bookmarkedList,")

with open("app/src/main/java/com/example/ui/MainHomeScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "r") as f:
    content = f.read()

content = content.replace("when (val slide = interleavedSlides[vPage]) {\n                when (val slide = interleavedSlides[vPage]) {", "when (val slide = interleavedSlides[vPage]) {")
content = content.replace("allNewsList = newsList", "allNewsList = allNewsList")

with open("app/src/main/java/com/example/ui/components/InshortsFeedView.kt", "w") as f:
    f.write(content)

