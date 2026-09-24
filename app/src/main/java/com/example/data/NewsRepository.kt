package com.example.data

import com.example.model.NewsArticle
import com.example.model.NewsNotification
import com.example.model.SavedNews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NewsRepository(private val newsDao: NewsDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (newsDao.getTotalNewsCount() == 0) {
                newsDao.insertAllNews(DefaultNewsData.INITIAL_ARTICLES)
                DefaultNewsData.INITIAL_NOTIFICATIONS.forEach { notif ->
                    newsDao.insertNotification(notif)
                }
            }
        }
    }

    fun getPublishedNews(): Flow<List<NewsArticle>> = newsDao.getPublishedNews()

    fun getBreakingNews(): Flow<List<NewsArticle>> = newsDao.getBreakingNews()

    fun getTrendingNews(): Flow<List<NewsArticle>> = newsDao.getTrendingNews()

    fun getNewsByCategory(category: String): Flow<List<NewsArticle>> {
        return if (category.equals("All", ignoreCase = true)) {
            newsDao.getPublishedNews()
        } else {
            newsDao.getNewsByCategory(category)
        }
    }

    fun getAllNewsAdmin(): Flow<List<NewsArticle>> = newsDao.getAllNewsAdmin()

    fun getNewsById(id: String): Flow<NewsArticle?> = newsDao.getNewsById(id)

    suspend fun getNewsByIdOnce(id: String): NewsArticle? = newsDao.getNewsByIdOnce(id)

    fun searchNews(query: String): Flow<List<NewsArticle>> = newsDao.searchNews(query.trim())

    fun getRelatedNews(category: String, excludeId: String): Flow<List<NewsArticle>> =
        newsDao.getRelatedNews(category, excludeId)

    fun isNewsSaved(id: String): Flow<Boolean> = newsDao.isNewsSaved(id)

    fun getSavedNewsArticles(): Flow<List<NewsArticle>> = newsDao.getSavedNewsArticles()

    suspend fun toggleSaveNews(id: String): Boolean {
        val isSaved = newsDao.isNewsSaved(id).firstOrNull() ?: false
        if (isSaved) {
            newsDao.removeSavedNews(id)
            return false
        } else {
            newsDao.saveNews(SavedNews(newsId = id))
            return true
        }
    }

    suspend fun incrementViews(id: String) {
        newsDao.incrementViews(id)
    }

    suspend fun incrementShares(id: String) {
        newsDao.incrementShares(id)
    }

    suspend fun insertNews(news: NewsArticle) {
        newsDao.insertNews(news)
    }

    suspend fun updateNews(news: NewsArticle) {
        newsDao.updateNews(news)
    }

    suspend fun deleteNews(id: String) {
        newsDao.deleteNewsById(id)
    }

    fun getAllNotifications(): Flow<List<NewsNotification>> = newsDao.getAllNotifications()

    fun getUnreadNotificationsCount(): Flow<Int> = newsDao.getUnreadNotificationsCount()

    suspend fun insertNotification(notification: NewsNotification) {
        newsDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: String) {
        newsDao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        newsDao.markAllNotificationsAsRead()
    }

    suspend fun clearAllNotifications() {
        newsDao.clearAllNotifications()
    }
}
