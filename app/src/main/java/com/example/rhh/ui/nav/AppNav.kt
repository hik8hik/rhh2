package com.example.rhh.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Border
import com.example.rhh.ui.theme.Surface
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.screens.HomeScreen
import com.example.rhh.ui.screens.RecordsScreen
import com.example.rhh.ui.screens.SettingsScreen
import com.example.rhh.ui.screens.SummaryScreen
import com.example.rhh.vm.HomeViewModel
import com.example.rhh.vm.RecordsViewModel
import com.example.rhh.vm.SettingsViewModel
import com.example.rhh.vm.SummaryViewModel
import androidx.compose.runtime.collectAsState

sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Tab("home", "HOME", Icons.Filled.Home)
    data object Records : Tab("records", "RECORDS", Icons.Filled.List)
    data object Summary : Tab("summary", "SUMMARY", Icons.Filled.ShowChart)
    data object Settings : Tab("settings", "SETTINGS", Icons.Filled.Settings)
}

private val TABS = listOf(Tab.Home, Tab.Records, Tab.Summary, Tab.Settings)

@Composable
fun MainTabs(onSignOut: () -> Unit) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Tab.Home.route

    val homeVm: HomeViewModel = viewModel()
    val recordsVm: RecordsViewModel = viewModel()
    val summaryVm: SummaryViewModel = viewModel()
    val settingsVm: SettingsViewModel = viewModel()

    val homeState by homeVm.state.collectAsState()
    val recordsState by recordsVm.state.collectAsState()
    val summaryState by summaryVm.state.collectAsState()
    val settingsState by settingsVm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        NavHost(
            navController = nav,
            startDestination = Tab.Home.route,
            modifier = Modifier.fillMaxSize().padding(bottom = 76.dp)
        ) {
            composable(Tab.Home.route) {
                HomeScreen(
                    buckets = homeState.hourlyBuckets,
                    todayProfit = homeState.todayProfit,
                    todayTrips = homeState.todayTrips,
                    refreshing = homeState.refreshing,
                    onRefresh = { homeVm.refresh() },
                    onStartTracking = { nav.navigate(Tab.Records.route) }
                )
            }
            composable(Tab.Records.route) {
                RecordsScreen(trips = recordsState.trips, loading = recordsState.loading)
            }
            composable(Tab.Summary.route) {
                SummaryScreen(
                    loading = summaryState.loading,
                    series = summaryState.series,
                    total = summaryState.total
                )
            }
            composable(Tab.Settings.route) {
                SettingsScreen(
                    user = settingsState.user,
                    sub = settingsState.sub,
                    onSignOut = onSignOut
                )
            }
        }

        NavigationBar(
            containerColor = Surface,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(0.dp)
        ) {
            TABS.forEach { tab ->
                val selected = currentRoute == tab.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        nav.navigate(tab.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(tab.icon, contentDescription = tab.label, tint = if (selected) Accent else TextDim) },
                    label = { Text(tab.label, color = if (selected) Accent else TextDim) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Accent.copy(alpha = 0.14f)
                    )
                )
            }
        }
    }
}
