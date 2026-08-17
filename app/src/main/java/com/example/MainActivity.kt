package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DailyScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SourcesScreen
import com.example.ui.theme.ZadAlYoumTheme
import com.example.ui.viewmodel.ZadViewModel

enum class MainNavDestination(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    DAILY("الزاد", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    FAVORITES("المفضلة", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
    SEARCH("البحث", Icons.Filled.Search, Icons.Outlined.Search),
    SOURCES("المصادر", Icons.Filled.Verified, Icons.Outlined.Verified),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: ZadViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val openDay = intent?.getIntExtra("OPEN_DAY", -1) ?: -1
        if (openDay in 1..365) {
            viewModel.setDay(openDay)
        }

        setContent {
            ZadAlYoumTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    ZadAlYoumApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        val openDay = intent.getIntExtra("OPEN_DAY", -1)
        if (openDay in 1..365) {
            viewModel.setDay(openDay)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZadAlYoumApp(viewModel: ZadViewModel) {
    var currentDestination by remember { mutableStateOf(MainNavDestination.DAILY) }

    val dailyBundle by viewModel.dailyBundle.collectAsState()
    val currentDay by viewModel.currentDay.collectAsState()
    val selectedTab by viewModel.selectedSectionTab.collectAsState()
    val favoriteKeys by viewModel.favoriteKeysSet.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedFavFilter by viewModel.selectedFavoriteFilter.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val sources by viewModel.sources.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val completedDaysCount by viewModel.completedDaysCount.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "زاد",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "زاد اليوم",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "٣×٣",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                MainNavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                MainNavDestination.DAILY -> {
                    DailyScreen(
                        viewModel = viewModel,
                        dailyBundle = dailyBundle,
                        currentDay = currentDay,
                        favoriteKeys = favoriteKeys,
                        fontScale = appSettings.arabicFontSizeScale,
                        preferredMadhhab = appSettings.preferredMadhhab,
                        selectedTab = selectedTab
                    )
                }

                MainNavDestination.FAVORITES -> {
                    FavoritesScreen(
                        viewModel = viewModel,
                        favorites = favorites,
                        selectedFilter = selectedFavFilter,
                        onNavigateToDay = { day ->
                            viewModel.setDay(day)
                            currentDestination = MainNavDestination.DAILY
                        }
                    )
                }

                MainNavDestination.SEARCH -> {
                    SearchScreen(
                        viewModel = viewModel,
                        searchState = searchState,
                        onNavigateToDay = { day ->
                            viewModel.setDay(day)
                            currentDestination = MainNavDestination.DAILY
                        }
                    )
                }

                MainNavDestination.SOURCES -> {
                    SourcesScreen(
                        sources = sources
                    )
                }

                MainNavDestination.SETTINGS -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        settings = appSettings,
                        completedDaysCount = completedDaysCount
                    )
                }
            }
        }
    }
}
