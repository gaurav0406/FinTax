package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioSpeechManager
import com.example.data.AppDatabase
import com.example.data.FinancialNewsEntity
import com.example.data.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).financialNewsDao()
    private val repository = NewsRepository(dao)
    val audioSpeechManager: AudioSpeechManager = AudioSpeechManager(application)

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Feed, 1: Bookmarks, 2: AI Summarizer, 3: Python Pipeline
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow("")
    val aiStatusMessage: StateFlow<String> = _aiStatusMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val newsList: StateFlow<List<FinancialNewsEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isNotBlank()) {
            repository.searchNews(query)
        } else {
            _selectedCategory.flatMapLatest { category ->
                repository.getNewsByCategory(category)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookmarkedNews: StateFlow<List<FinancialNewsEntity>> = repository.bookmarkedNews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userProfile = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allComments = repository.allComments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getCommentsForNews(newsId: Int) = repository.getCommentsForNews(newsId)

    fun saveUserProfile(profile: com.example.data.UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun addComment(newsId: Int, text: String, parentCommentId: Int? = null, taggedUser: String? = null) {
        val currentProfile = userProfile.value
        val name = currentProfile?.userName?.ifBlank { "User" } ?: "Gaurav Sharma"
        val city = currentProfile?.city?.ifBlank { "India" } ?: "Mumbai"
        viewModelScope.launch {
            repository.addComment(newsId, text, parentCommentId, taggedUser, name, city)
        }
    }

    fun upvoteComment(commentId: Int) {
        viewModelScope.launch {
            repository.upvoteComment(commentId)
        }
    }

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        _searchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun toggleBookmark(news: FinancialNewsEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(news.id, news.isBookmarked)
        }
    }

    fun playAudio(news: FinancialNewsEntity) {
        audioSpeechManager.playAudio(
            newsId = news.id,
            title = news.title,
            textToSpeak = news.summaryText
        )
    }

    fun processRawNewsWithGemini(rawText: String, sourceUrl: String) {
        if (rawText.isBlank()) return
        viewModelScope.launch {
            _isGeneratingAi.value = true
            _aiStatusMessage.value = "Extracting Indian tax insights & converting to 60-word audio..."
            val result = repository.processAndInsertNews(rawText, sourceUrl)
            _isGeneratingAi.value = false
            if (result.isSuccess) {
                _aiStatusMessage.value = "Success! Summary added to Feed & Audio generated."
                _activeTab.value = 0 // Switch back to feed
            } else {
                _aiStatusMessage.value = "Failed to process text. Try again."
            }
        }
    }

    fun refreshFeeds() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _aiStatusMessage.value = "Refreshing live Indian financial feeds..."
            kotlinx.coroutines.delay(800)
            
            // Fetch live news from the backend
            repository.fetchLiveNewsFromSupabase()
            
            // Seed sample data if database is still empty after live fetch
            repository.seedInitialDataIfEmpty()
            
            _aiStatusMessage.value = "Feeds updated!"
            _isRefreshing.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioSpeechManager.shutdown()
    }
}
