package com.learnpulse.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.learnpulse.ui.screens.catalog.CatalogScreen
import com.learnpulse.ui.screens.downloads.DownloadsScreen
import com.learnpulse.ui.screens.home.HomeScreen
import com.learnpulse.ui.screens.notes.NotesScreen
import com.learnpulse.ui.screens.profile.ProfileScreen
import com.learnpulse.ui.screens.progress.ProgressScreen
import cafe.adriel.voyager.navigator.tab.TabOptions
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember { TabOptions(index = 0u, title = "Home") }

    @Composable
    override fun Content() {
        Navigator(HomeScreen()) { navigator -> SlideTransition(navigator) }
    }
}

object CatalogTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember { TabOptions(index = 1u, title = "Catalog") }

    @Composable
    override fun Content() {
        Navigator(CatalogScreen()) { navigator -> SlideTransition(navigator) }
    }
}

object ProgressTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember { TabOptions(index = 2u, title = "Progress") }

    @Composable
    override fun Content() {
        Navigator(ProgressScreen()) { navigator -> SlideTransition(navigator) }
    }
}

object NotesTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember { TabOptions(index = 3u, title = "Notes") }

    @Composable
    override fun Content() {
        Navigator(NotesScreen()) { navigator -> SlideTransition(navigator) }
    }
}

object DownloadsTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember { TabOptions(index = 4u, title = "Downloads") }

    @Composable
    override fun Content() {
        Navigator(DownloadsScreen()) { navigator -> SlideTransition(navigator) }
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() = remember { TabOptions(index = 5u, title = "Profile") }

    @Composable
    override fun Content() {
        Navigator(ProfileScreen()) { navigator -> SlideTransition(navigator) }
    }
}

@Composable
fun AppNavigation() {
    val tabs = listOf(HomeTab, CatalogTab, ProgressTab, NotesTab, DownloadsTab, ProfileTab)
    val tabIcons = listOf(
        Icons.Filled.Home to Icons.Outlined.Home,
        Icons.Filled.Explore to Icons.Outlined.Explore,
        Icons.Filled.ShowChart to Icons.Outlined.ShowChart,
        Icons.Filled.Notes to Icons.Outlined.Notes,
        Icons.Filled.Download to Icons.Outlined.Download,
        Icons.Filled.Person to Icons.Outlined.Person
    )

    TabNavigator(HomeTab) {
        val tabNavigator = LocalTabNavigator.current
        Scaffold(
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = tabNavigator.current == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { tabNavigator.current = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tabIcons[index].first else tabIcons[index].second,
                                    contentDescription = tab.options.title
                                )
                            },
                            label = { Text(tab.options.title) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                CurrentTab()
            }
        }
    }
}
