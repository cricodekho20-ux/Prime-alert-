package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.NewsRepository
import com.example.model.NewsArticle
import com.example.model.NewsNotification
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NewsRepository(db.newsDao())
    }

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val currentCategoryNews: StateFlow<List<NewsArticle>> = _selectedCategory
        .flatMapLatest { category ->
            repository.getNewsByCategory(category)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val breakingNews: StateFlow<List<NewsArticle>> = repository.getBreakingNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingNews: StateFlow<List<NewsArticle>> = repository.getTrendingNews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedNews: StateFlow<List<NewsArticle>> = repository.getSavedNewsArticles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NewsNotification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.getUnreadNotificationsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<NewsArticle>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getPublishedNews()
            } else {
                repository.searchNews(query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inAppAlertBanner = MutableStateFlow<NewsNotification?>(null)
    val inAppAlertBanner: StateFlow<NewsNotification?> = _inAppAlertBanner.asStateFlow()

    private val _fontSizeMultiplier = MutableStateFlow(1.0f)
    val fontSizeMultiplier: StateFlow<Float> = _fontSizeMultiplier.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getNewsById(id: String) = repository.getNewsById(id)

    fun getRelatedNews(category: String, currentId: String) =
        repository.getRelatedNews(category, currentId)

    fun isNewsSaved(id: String) = repository.isNewsSaved(id)

    fun toggleSave(newsId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val isNowSaved = repository.toggleSaveNews(newsId)
            onResult(isNowSaved)
        }
    }

    fun recordView(newsId: String) {
        viewModelScope.launch {
            repository.incrementViews(newsId)
        }
    }

    fun recordShare(newsId: String) {
        viewModelScope.launch {
            repository.incrementShares(newsId)
        }
    }

    fun showInAppAlert(notification: NewsNotification) {
        _inAppAlertBanner.value = notification
    }

    fun dismissInAppAlert() {
        _inAppAlertBanner.value = null
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    fun increaseFontSize() {
        if (_fontSizeMultiplier.value < 1.4f) {
            _fontSizeMultiplier.value += 0.15f
        }
    }

    fun decreaseFontSize() {
        if (_fontSizeMultiplier.value > 0.85f) {
            _fontSizeMultiplier.value -= 0.15f
        }
    }

    fun resetFontSize() {
        _fontSizeMultiplier.value = 1.0f
    }
}
