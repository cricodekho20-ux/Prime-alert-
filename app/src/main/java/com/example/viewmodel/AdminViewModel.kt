package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.NewsRepository
import com.example.model.NewsArticle
import com.example.model.NewsNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class AdminDashboardStats(
    val totalNews: Int = 0,
    val todayNews: Int = 0,
    val totalViews: Int = 0,
    val breakingCount: Int = 0,
    val totalUsers: Int = 18450,
    val todayViews: Int = 0,
    val categoryCounts: Map<String, Int> = emptyMap()
)

data class NewsFormState(
    val id: String = "",
    val headline: String = "",
    val shortDescription: String = "",
    val content: String = "",
    val imageUrl: String = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=800&auto=format&fit=crop&q=80",
    val videoUrl: String = "",
    val category: String = "India",
    val tags: String = "",
    val author: String = "Prime Alert Desk",
    val isBreaking: Boolean = false,
    val sendNotification: Boolean = true,
    val status: String = "Published",
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NewsRepository(db.newsDao())
    }

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    val allNews: StateFlow<List<NewsArticle>> = repository.getAllNewsAdmin()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardStats: StateFlow<AdminDashboardStats> = allNews.map { list ->
        val now = System.currentTimeMillis()
        val oneDayAgo = now - 24 * 60 * 60 * 1000L
        val todayItems = list.filter { it.publishedAt >= oneDayAgo }
        val totalViews = list.sumOf { it.views }
        val todayViews = (totalViews * 0.28).toInt() + 1420
        val breakingCount = list.count { it.isBreaking }
        val catCounts = list.groupBy { it.category }.mapValues { it.value.size }

        AdminDashboardStats(
            totalNews = list.size,
            todayNews = todayItems.size,
            totalViews = totalViews,
            breakingCount = breakingCount,
            totalUsers = (totalViews * 0.65).toInt() + 8500,
            todayViews = todayViews,
            categoryCounts = catCounts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminDashboardStats())

    private val _formState = MutableStateFlow(NewsFormState())
    val formState: StateFlow<NewsFormState> = _formState.asStateFlow()

    fun login(pinOrPass: String): Boolean {
        return if (pinOrPass.trim() == "2026" || pinOrPass.trim().equals("admin", ignoreCase = true) || pinOrPass.trim() == "admin123") {
            _isAdminLoggedIn.value = true
            _loginError.value = null
            true
        } else {
            _loginError.value = "अमान्य पिन / पासवर्ड। कृपया '2026' दर्ज करें।"
            false
        }
    }

    fun logout() {
        _isAdminLoggedIn.value = false
    }

    fun prepareNewArticle() {
        _formState.value = NewsFormState(
            id = "news-" + UUID.randomUUID().toString().take(8),
            isEditMode = false
        )
    }

    fun prepareEditArticle(article: NewsArticle) {
        _formState.value = NewsFormState(
            id = article.id,
            headline = article.headline,
            shortDescription = article.shortDescription,
            content = article.content,
            imageUrl = article.imageUrl,
            videoUrl = article.videoUrl,
            category = article.category,
            tags = article.tags,
            author = article.author,
            isBreaking = article.isBreaking,
            sendNotification = false,
            status = article.status,
            isEditMode = true
        )
    }

    fun updateForm(transform: (NewsFormState) -> NewsFormState) {
        _formState.value = transform(_formState.value)
    }

    fun publishOrUpdateNews(
        onSuccess: (NewsNotification?) -> Unit
    ) {
        val form = _formState.value

        if (form.headline.isBlank()) {
            _formState.value = form.copy(errorMessage = "कृपया शीर्षक (Headline) दर्ज करें")
            return
        }
        if (form.content.isBlank()) {
            _formState.value = form.copy(errorMessage = "कृपया पूरी खबर (Content) दर्ज करें")
            return
        }

        val articleId = if (form.id.isBlank()) "news-" + UUID.randomUUID().toString().take(8) else form.id
        val now = System.currentTimeMillis()

        val article = NewsArticle(
            id = articleId,
            headline = form.headline.trim(),
            shortDescription = if (form.shortDescription.isNotBlank()) form.shortDescription.trim() else form.headline.take(90),
            content = form.content.trim(),
            imageUrl = form.imageUrl.ifBlank { "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=800&auto=format&fit=crop&q=80" },
            videoUrl = form.videoUrl.trim(),
            category = form.category,
            tags = form.tags.trim(),
            author = form.author.ifBlank { "Prime Alert Desk" },
            publishedAt = now,
            updatedAt = now,
            isBreaking = form.isBreaking,
            views = if (form.isEditMode) 0 else 1,
            shares = 0,
            status = form.status
        )

        viewModelScope.launch {
            if (form.isEditMode) {
                repository.updateNews(article)
            } else {
                repository.insertNews(article)
            }

            var createdNotification: NewsNotification? = null
            if (form.sendNotification && form.status == "Published") {
                val notif = NewsNotification(
                    id = "notif-" + UUID.randomUUID().toString().take(8),
                    title = if (form.isBreaking) "🔴 BREAKING: ${article.headline.take(45)}" else "📢 नई खबर: ${article.headline.take(45)}",
                    message = article.shortDescription.take(90),
                    newsId = article.id,
                    createdAt = now,
                    isRead = false
                )
                repository.insertNotification(notif)
                createdNotification = notif
            }

            _formState.value = _formState.value.copy(
                successMessage = if (form.isEditMode) "खबर सफलतापूर्वक अपडेट हो गई!" else "खबर सफलतापूर्वक प्रकाशित हो गई!",
                errorMessage = null
            )
            onSuccess(createdNotification)
        }
    }

    fun deleteNews(id: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteNews(id)
            onComplete()
        }
    }

    fun sendBroadcastNotification(title: String, message: String, targetNewsId: String = "", onSent: (NewsNotification) -> Unit) {
        val notif = NewsNotification(
            id = "notif-" + UUID.randomUUID().toString().take(8),
            title = title.trim(),
            message = message.trim(),
            newsId = targetNewsId,
            createdAt = System.currentTimeMillis(),
            isRead = false
        )
        viewModelScope.launch {
            repository.insertNotification(notif)
            onSent(notif)
        }
    }
}
