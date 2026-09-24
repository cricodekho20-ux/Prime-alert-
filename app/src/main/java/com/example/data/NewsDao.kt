package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.NewsArticle
import com.example.model.NewsNotification
import com.example.model.SavedNews
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_articles WHERE status = 'Published' ORDER BY publishedAt DESC")
    fun getPublishedNews(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE status = 'Published' AND isBreaking = 1 ORDER BY publishedAt DESC")
    fun getBreakingNews(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE status = 'Published' ORDER BY views DESC, publishedAt DESC LIMIT 20")
    fun getTrendingNews(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE status = 'Published' AND category = :category ORDER BY publishedAt DESC")
    fun getNewsByCategory(category: String): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC")
    fun getAllNewsAdmin(): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE id = :id LIMIT 1")
    fun getNewsById(id: String): Flow<NewsArticle?>

    @Query("SELECT * FROM news_articles WHERE id = :id LIMIT 1")
    suspend fun getNewsByIdOnce(id: String): NewsArticle?

    @Query("""
        SELECT * FROM news_articles 
        WHERE status = 'Published' 
        AND (headline LIKE '%' || :query || '%' 
             OR shortDescription LIKE '%' || :query || '%' 
             OR content LIKE '%' || :query || '%' 
             OR tags LIKE '%' || :query || '%' 
             OR category LIKE '%' || :query || '%')
        ORDER BY publishedAt DESC
    """)
    fun searchNews(query: String): Flow<List<NewsArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsArticle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(list: List<NewsArticle>)

    @Update
    suspend fun updateNews(news: NewsArticle)

    @Query("DELETE FROM news_articles WHERE id = :id")
    suspend fun deleteNewsById(id: String)

    @Query("UPDATE news_articles SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Query("UPDATE news_articles SET shares = shares + 1 WHERE id = :id")
    suspend fun incrementShares(id: String)

    @Query("SELECT * FROM news_articles WHERE status = 'Published' AND category = :category AND id != :excludeId ORDER BY publishedAt DESC LIMIT 4")
    fun getRelatedNews(category: String, excludeId: String): Flow<List<NewsArticle>>

    // Saved News
    @Query("SELECT EXISTS(SELECT 1 FROM saved_news WHERE newsId = :id)")
    fun isNewsSaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNews(saved: SavedNews)

    @Query("DELETE FROM saved_news WHERE newsId = :newsId")
    suspend fun removeSavedNews(newsId: String)

    @Query("""
        SELECT a.* FROM news_articles a 
        INNER JOIN saved_news s ON a.id = s.newsId 
        ORDER BY s.savedAt DESC
    """)
    fun getSavedNewsArticles(): Flow<List<NewsArticle>>

    // Notifications
    @Query("SELECT * FROM news_notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NewsNotification>>

    @Query("SELECT COUNT(*) FROM news_notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NewsNotification)

    @Query("UPDATE news_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE news_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM news_notifications")
    suspend fun clearAllNotifications()

    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getTotalNewsCount(): Int
}
