package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey val id: String,
    val headline: String,
    val shortDescription: String,
    val content: String,
    val imageUrl: String,
    val videoUrl: String = "",
    val category: String,
    val tags: String = "",
    val author: String = "Prime Alert Desk",
    val publishedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isBreaking: Boolean = false,
    val views: Int = 0,
    val shares: Int = 0,
    val status: String = "Published" // "Published", "Draft", "Archived"
)

@Entity(tableName = "saved_news")
data class SavedNews(
    @PrimaryKey val newsId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "news_notifications")
data class NewsNotification(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val newsId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class NewsCategory(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val iconName: String,
    val colorHex: Long = 0xFFD32F2F
)

object NewsCategories {
    val ALL = listOf(
        NewsCategory("all", "All", "सभी", "AllInclusive"),
        NewsCategory("india", "India", "देश / भारत", "Flag", 0xFFE65100),
        NewsCategory("bihar", "Bihar", "बिहार", "LocationCity", 0xFF1565C0),
        NewsCategory("politics", "Politics", "राजनीति", "AccountBalance", 0xFF6A1B9A),
        NewsCategory("cricket", "Cricket", "क्रिकेट", "SportsCricket", 0xFF2E7D32),
        NewsCategory("sports", "Sports", "खेल", "EmojiEvents", 0xFF00838F),
        NewsCategory("world", "World", "विदेश", "Public", 0xFF00695C),
        NewsCategory("entertainment", "Entertainment", "मनोरंजन", "Movie", 0xFFAD1457),
        NewsCategory("technology", "Technology", "टेक", "Devices", 0xFF283593),
        NewsCategory("business", "Business", "बिजनेस", "TrendingUp", 0xFF37474F),
        NewsCategory("education", "Education", "शिक्षा", "School", 0xFFC2185B)
    )

    fun getCategoryNames(): List<String> = ALL.filter { it.nameEn != "All" }.map { it.nameEn }
}
