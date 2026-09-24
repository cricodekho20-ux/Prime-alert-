package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.model.NewsArticle
import com.example.ui.components.InAppNotificationBanner
import com.example.ui.components.ShareBottomSheet
import com.example.ui.screens.AdminAddEditNewsScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NewsDetailScreen
import com.example.ui.screens.NotificationsBottomSheet
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedNewsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.TrendingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimeRed
import com.example.viewmodel.AdminViewModel
import com.example.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

sealed class Screen(
    val route: String,
    val labelHi: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "होम", Icons.Filled.Home, Icons.Outlined.Home)
    data object Categories : Screen("categories", "श्रेणियां", Icons.Filled.Category, Icons.Outlined.Category)
    data object Trending : Screen("trending", "ट्रेंडिंग", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment)
    data object Saved : Screen("saved", "सहेजे गए", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    data object Profile : Screen("profile", "प्रोफ़ाइल", Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {

    private val newsViewModel: NewsViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                // State for sheets & alerts
                var articleToShare by remember { mutableStateOf<NewsArticle?>(null) }
                var showNotificationsSheet by remember { mutableStateOf(false) }

                val inAppAlert by newsViewModel.inAppAlertBanner.collectAsState()
                val isAdminLoggedIn by adminViewModel.isAdminLoggedIn.collectAsState()

                val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val notificationsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                val bottomBarRoutes = listOf(
                    Screen.Home.route,
                    Screen.Categories.route,
                    Screen.Trending.route,
                    Screen.Saved.route,
                    Screen.Profile.route
                )
                val showBottomBar = currentRoute in bottomBarRoutes

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 6.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bottom_navigation_bar")
                            ) {
                                val items = listOf(
                                    Screen.Home,
                                    Screen.Categories,
                                    Screen.Trending,
                                    Screen.Saved,
                                    Screen.Profile
                                )

                                items.forEach { screen ->
                                    val isSelected = currentRoute == screen.route

                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.labelHi
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = screen.labelHi,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = PrimeRed,
                                            selectedTextColor = PrimeRed,
                                            indicatorColor = PrimeRed.copy(alpha = 0.12f),
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_item_${screen.route}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 1. Home Screen
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    viewModel = newsViewModel,
                                    onNewsClick = { newsId ->
                                        navController.navigate("news_detail/$newsId")
                                    },
                                    onSearchClick = {
                                        navController.navigate("search")
                                    },
                                    onNotificationClick = {
                                        showNotificationsSheet = true
                                    },
                                    onShareClick = { article ->
                                        articleToShare = article
                                    }
                                )
                            }

                            // 2. Categories Screen
                            composable(Screen.Categories.route) {
                                CategoriesScreen(
                                    viewModel = newsViewModel,
                                    onCategoryClick = { categoryName ->
                                        newsViewModel.selectCategory(categoryName)
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Home.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 3. Trending Screen
                            composable(Screen.Trending.route) {
                                TrendingScreen(
                                    viewModel = newsViewModel,
                                    onNewsClick = { newsId ->
                                        navController.navigate("news_detail/$newsId")
                                    },
                                    onShareClick = { article ->
                                        articleToShare = article
                                    }
                                )
                            }

                            // 4. Saved News Screen
                            composable(Screen.Saved.route) {
                                SavedNewsScreen(
                                    viewModel = newsViewModel,
                                    onNewsClick = { newsId ->
                                        navController.navigate("news_detail/$newsId")
                                    },
                                    onShareClick = { article ->
                                        articleToShare = article
                                    },
                                    onExploreClick = {
                                        navController.navigate(Screen.Home.route)
                                    }
                                )
                            }

                            // 5. Profile Screen
                            composable(Screen.Profile.route) {
                                ProfileScreen(
                                    newsViewModel = newsViewModel,
                                    adminViewModel = adminViewModel,
                                    onAdminClick = {
                                        if (isAdminLoggedIn) {
                                            navController.navigate("admin_dashboard")
                                        } else {
                                            navController.navigate("admin_login")
                                        }
                                    }
                                )
                            }

                            // 6. News Detail Screen
                            composable(
                                route = "news_detail/{newsId}",
                                arguments = listOf(navArgument("newsId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
                                NewsDetailScreen(
                                    newsId = newsId,
                                    viewModel = newsViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onRelatedNewsClick = { relId ->
                                        navController.navigate("news_detail/$relId")
                                    },
                                    onShareClick = { article ->
                                        articleToShare = article
                                    }
                                )
                            }

                            // 7. Search Screen
                            composable("search") {
                                SearchScreen(
                                    viewModel = newsViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onNewsClick = { newsId ->
                                        navController.navigate("news_detail/$newsId")
                                    }
                                )
                            }

                            // 8. Admin Login Screen
                            composable("admin_login") {
                                AdminLoginScreen(
                                    adminViewModel = adminViewModel,
                                    onLoginSuccess = {
                                        navController.navigate("admin_dashboard") {
                                            popUpTo("admin_login") { inclusive = true }
                                        }
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            // 9. Admin Dashboard Screen
                            composable("admin_dashboard") {
                                AdminDashboardScreen(
                                    adminViewModel = adminViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onAddNewClick = {
                                        adminViewModel.prepareNewArticle()
                                        navController.navigate("admin_add_edit")
                                    },
                                    onEditNewsClick = { article ->
                                        adminViewModel.prepareEditArticle(article)
                                        navController.navigate("admin_add_edit")
                                    },
                                    onViewNewsClick = { newsId ->
                                        navController.navigate("news_detail/$newsId")
                                    },
                                    onBroadcastSent = { notif ->
                                        newsViewModel.showInAppAlert(notif)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("पुश नोटिफिकेशन सफलतापूर्वक भेजा गया!")
                                        }
                                    }
                                )
                            }

                            // 10. Admin Add/Edit News Screen
                            composable("admin_add_edit") {
                                AdminAddEditNewsScreen(
                                    adminViewModel = adminViewModel,
                                    onBackClick = { navController.popBackStack() },
                                    onPublishSuccess = { createdNotif ->
                                        if (createdNotif != null) {
                                            newsViewModel.showInAppAlert(createdNotif)
                                        }
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("खबर सफलतापूर्वक पब्लिश हो गई!")
                                        }
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        // Top Floating In-App Push Notification Banner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                        ) {
                            InAppNotificationBanner(
                                notification = inAppAlert,
                                onNotificationClick = { newsId ->
                                    navController.navigate("news_detail/$newsId")
                                },
                                onDismiss = { newsViewModel.dismissInAppAlert() }
                            )
                        }
                    }
                }

                // Share Bottom Sheet
                if (articleToShare != null) {
                    ShareBottomSheet(
                        article = articleToShare!!,
                        sheetState = shareSheetState,
                        onDismiss = { articleToShare = null }
                    )
                }

                // Notifications Bottom Sheet
                if (showNotificationsSheet) {
                    NotificationsBottomSheet(
                        viewModel = newsViewModel,
                        sheetState = notificationsSheetState,
                        onNotificationClick = { targetNewsId: String ->
                            showNotificationsSheet = false
                            navController.navigate("news_detail/$targetNewsId")
                        },
                        onDismiss = { showNotificationsSheet = false }
                    )
                }
            }
        }
    }
}
