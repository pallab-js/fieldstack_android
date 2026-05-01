package com.fieldstack.android

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fieldstack.android.ui.auth.AuthViewModel
import com.fieldstack.android.ui.auth.LoginScreen
import com.fieldstack.android.ui.components.AppTopBar
import com.fieldstack.android.ui.components.SyncBadge
import com.fieldstack.android.ui.components.SyncBadgeState
import com.fieldstack.android.ui.dashboard.DashboardScreen
import com.fieldstack.android.ui.dashboard.InsightsScreen
import com.fieldstack.android.ui.reports.ReportBuilderScreen
import com.fieldstack.android.ui.settings.SettingsScreen
import com.fieldstack.android.ui.sync.SyncScreen
import com.fieldstack.android.ui.tasks.TaskDetailScreen
import com.fieldstack.android.ui.tasks.TaskListScreen

import com.fieldstack.android.ui.theme.Mint
import com.fieldstack.android.ui.theme.Stone
import com.fieldstack.android.ui.theme.StoneLight

private data class NavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val NAV_ITEMS = listOf(
    NavItem(Screen.Dashboard, "Dashboard", Icons.Filled.Dashboard,  Icons.Outlined.Dashboard),
    NavItem(Screen.TaskList,  "Tasks",     Icons.Filled.Assignment, Icons.Outlined.Assignment),
    NavItem(Screen.Sync,      "Sync",      Icons.Filled.Sync,       Icons.Outlined.Sync),
    NavItem(Screen.Settings,  "Settings",  Icons.Filled.Settings,   Icons.Outlined.Settings),
)

private val TOP_LEVEL_ROUTES = NAV_ITEMS.map { it.screen.route }.toSet()

@Composable
fun FieldStackNavHost() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val start = if (authViewModel.isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showChrome = currentRoute in TOP_LEVEL_ROUTES
    val topBarTitle = NAV_ITEMS.find { it.screen.route == currentRoute }?.label ?: "FieldStack"
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        topBar = {
            if (showChrome && currentRoute != Screen.Dashboard.route) {
                AppTopBar(
                    title = topBarTitle,
                    syncBadge = { SyncBadge(SyncBadgeState.Synced) },
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                NavigationBar(containerColor = StoneLight) {
                    NAV_ITEMS.forEach { item ->
                        val selected = backStackEntry?.destination
                            ?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                )
                            },
                            label = { Text(item.label) },
                            modifier = Modifier.semantics { contentDescription = item.label },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = Mint,
                                selectedTextColor   = Mint,
                                unselectedIconColor = Stone,
                                unselectedTextColor = Stone,
                                indicatorColor      = StoneLight,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = start,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onTaskClick = { id -> navController.navigate(Screen.TaskDetail.route(id)) },
                    onNewReport = { navController.navigate(Screen.ReportBuilder.route("new")) },
                    onViewInsights = { navController.navigate(Screen.Insights.route) },
                )
            }
            composable(Screen.TaskList.route) {
                TaskListScreen(onTaskClick = { id ->
                    navController.navigate(Screen.TaskDetail.route(id))
                })
            }
            composable(Screen.Sync.route)     { SyncScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onAdminConsole = { navController.navigate(Screen.Admin.route) },
                )
            }

            composable(Screen.TaskDetail.route) { back ->
                val taskId = back.arguments?.getString("taskId") ?: return@composable
                TaskDetailScreen(
                    taskId = taskId,
                    onStartTask = { id -> navController.navigate(Screen.ReportBuilder.route(id)) },
                )
            }
            composable(Screen.ReportBuilder.route) { back ->
                val taskId = back.arguments?.getString("taskId") ?: return@composable
                ReportBuilderScreen(taskId = taskId)
            }
            composable(Screen.Insights.route) { InsightsScreen() }
            composable(Screen.Admin.route)   {
                com.fieldstack.android.ui.settings.AdminScreen()
            }
        }
    }
}
