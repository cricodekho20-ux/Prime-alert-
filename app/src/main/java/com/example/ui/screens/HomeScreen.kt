package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NewsArticle
import com.example.ui.components.AdMobBannerCard
import com.example.ui.components.BreakingNewsTicker
import com.example.ui.components.CategoryChipBar
import com.example.ui.components.NewsCard
import com.example.ui.components.NewsHeroCard
import com.example.ui.components.PrimeAlertHeader
import com.example.ui.theme.PrimeRed
import com.example.viewmodel.NewsViewModel

@Composable
fun HomeScreen(
    viewModel: NewsViewModel,
    onNewsClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onShareClick: (NewsArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val newsList by viewModel.currentCategoryNews.collectAsState()
    val breakingList by viewModel.breakingNews.collectAsState()
    val savedNews by viewModel.savedNews.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()

    val savedIds = remember(savedNews) { savedNews.map { it.id }.toSet() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        // Header
        PrimeAlertHeader(
            unreadCount = unreadCount,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onLogoClick = { viewModel.selectCategory("All") }
        )

        // Breaking News Ticker (🔴 BREAKING NEWS)
        BreakingNewsTicker(
            breakingNewsList = breakingList,
            onNewsClick = onNewsClick
        )

        // Category Filter Chips
        CategoryChipBar(
            selectedCategory = selectedCategory,
            onSelectCategory = { viewModel.selectCategory(it) }
        )

        if (newsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimeRed)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "खबरें लोड हो रही हैं...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home_news_list")
            ) {
                // Top Hero Featured Article (only shown when in 'All' or first article of category)
                item {
                    val heroArticle = newsList.firstOrNull()
                    if (heroArticle != null) {
                        NewsHeroCard(
                            article = heroArticle,
                            isSaved = savedIds.contains(heroArticle.id),
                            onNewsClick = onNewsClick,
                            onShareClick = onShareClick,
                            onSaveClick = { viewModel.toggleSave(it) }
                        )
                    }
                }

                item {
                    Text(
                        text = if (selectedCategory == "All") "ताज़ा खबरें (Latest News)" else "$selectedCategory की खबरें",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Remaining articles in feed
                val remainingArticles = if (newsList.size > 1) newsList.drop(1) else emptyList()
                itemsIndexed(remainingArticles, key = { _, item -> item.id }) { index, article ->
                    NewsCard(
                        article = article,
                        isSaved = savedIds.contains(article.id),
                        onNewsClick = onNewsClick,
                        onShareClick = onShareClick,
                        onSaveClick = { viewModel.toggleSave(it) }
                    )

                    // Insert AdMob Native/Banner after 2nd item in feed
                    if (index == 1) {
                        AdMobBannerCard()
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
