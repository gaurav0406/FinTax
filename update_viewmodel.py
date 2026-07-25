import re

with open('app/src/main/java/com/example/ui/NewsViewModel.kt', 'r') as f:
    text = f.read()

text = text.replace("import com.example.data.NewsRepository", "import com.example.data.NewsRepository\nimport com.example.BuildConfig")

text = text.replace("            repository.seedInitialDataIfEmpty()", """            // repository.seedInitialDataIfEmpty() // Sample data removed
            try {
                val apiKey = BuildConfig.YOUTUBE_API_KEY
                if (apiKey.isNotBlank() && apiKey != "dummy") {
                    repository.fetchYouTubeVideos(apiKey)
                }
            } catch (e: Exception) {
                _aiStatusMessage.value = "Init YouTube fetch failed: ${e.message}"
            }""")

with open('app/src/main/java/com/example/ui/NewsViewModel.kt', 'w') as f:
    f.write(text)
