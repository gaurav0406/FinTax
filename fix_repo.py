import re

with open('app/src/main/java/com/example/data/NewsRepository.kt', 'r') as f:
    text = f.read()

# Remove fetchYouTubeVideos
text = re.sub(r'suspend fun fetchYouTubeVideos\(apiKey: String\).*?\}\s*\} catch \(e: Exception\) \{\s*e\.printStackTrace\(\)\s*\}\s*\}', '', text, flags=re.DOTALL)

with open('app/src/main/java/com/example/data/NewsRepository.kt', 'w') as f:
    f.write(text)

with open('app/src/main/java/com/example/ui/NewsViewModel.kt', 'r') as f:
    text = f.read()

text = re.sub(r'repository\.fetchYouTubeVideos\(apiKey\)', '// repository.fetchYouTubeVideos(apiKey)', text)

with open('app/src/main/java/com/example/ui/NewsViewModel.kt', 'w') as f:
    f.write(text)
