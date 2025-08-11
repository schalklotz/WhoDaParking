package com.whodaparking.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.whodaparking.app.R
import com.whodaparking.app.ui.CameraSearchScreen
import com.whodaparking.app.ui.TextSearchScreen

enum class WhoDaParkingScreen(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
) {
    TextSearch(
        route = "text_search",
        titleRes = R.string.text_search_title,
        icon = Icons.Default.Search
    ),
    CameraSearch(
        route = "camera_search", 
        titleRes = R.string.camera_search_title,
        icon = Icons.Default.CameraAlt
    )
}

@Composable
fun WhoDaParkingNavigation(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            WhoDaParkingBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WhoDaParkingScreen.TextSearch.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(WhoDaParkingScreen.TextSearch.route) {
                TextSearchScreen()
            }
            composable(WhoDaParkingScreen.CameraSearch.route) {
                CameraSearchScreen()
            }
        }
    }
}

@Composable
private fun WhoDaParkingBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        WhoDaParkingScreen.values().forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(screen.titleRes)
                    )
                },
                label = { Text(stringResource(screen.titleRes)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}