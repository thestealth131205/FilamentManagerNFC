package de.letheapp.filamentmanager.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import de.letheapp.filamentmanager.R
import de.letheapp.filamentmanager.data.db.AppDatabase
import de.letheapp.filamentmanager.ui.editor.FilamentEditorScreen
import de.letheapp.filamentmanager.ui.editor.FilamentEditorViewModel
import de.letheapp.filamentmanager.ui.info.InfoScreen
import de.letheapp.filamentmanager.ui.list.FilamentListScreen
import de.letheapp.filamentmanager.ui.list.FilamentListViewModel
import de.letheapp.filamentmanager.ui.nfc.NfcScreen
import de.letheapp.filamentmanager.ui.nfc.NfcViewModel
import de.letheapp.filamentmanager.ui.scan.ScanScreen

private object Routes {
    const val LIST = "list"
    const val EDITOR = "editor?filamentId={filamentId}"
    const val SCAN = "scan"
    const val NFC = "nfc"
    const val INFO = "info"

    fun editorRoute(filamentId: Long? = null) = "editor?filamentId=${filamentId ?: -1}"
}

private data class BottomTab(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomTabs = listOf(
    BottomTab(Routes.LIST, R.string.nav_list, Icons.Filled.List),
    BottomTab(Routes.SCAN, R.string.nav_scan, Icons.Filled.PhotoCamera),
    BottomTab(Routes.NFC, R.string.nav_nfc, Icons.Filled.Nfc),
    BottomTab(Routes.INFO, R.string.nav_info, Icons.Filled.Info),
)

@Composable
fun AppNavigation(database: AppDatabase) {
    val navController = rememberNavController()

    // Jede Ziel-Route hat ihre eigene Scaffold+TopAppBar, die bereits den
    // Status-Bar-Inset reserviert — ohne contentWindowInsets=0 würde dieser
    // äußere Scaffold den gleichen Inset ein zweites Mal oben einrechnen.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            NavigationBar {
                bottomTabs.forEach { tab ->
                    val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LIST,
            modifier = androidx.compose.ui.Modifier.padding(padding),
        ) {
            composable(Routes.LIST) {
                val viewModel = remember { FilamentListViewModel(database.filamentDao()) }
                FilamentListScreen(
                    viewModel = viewModel,
                    onAddFilament = { navController.navigate(Routes.editorRoute()) },
                    onOpenFilament = { id -> navController.navigate(Routes.editorRoute(id)) },
                )
            }
            composable(
                route = Routes.EDITOR,
                arguments = listOf(
                    navArgument("filamentId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                ),
            ) { backStackEntry ->
                val filamentId = backStackEntry.arguments?.getLong("filamentId")
                    ?.takeIf { it >= 0 }
                val viewModel = remember(filamentId) { FilamentEditorViewModel(database, filamentId) }
                FilamentEditorScreen(
                    viewModel = viewModel,
                    isEditing = filamentId != null,
                    onSaved = { navController.popBackStack() },
                    onDeleted = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.SCAN) {
                ScanScreen(onScanned = { navController.navigate(Routes.editorRoute()) })
            }
            composable(Routes.NFC) {
                val viewModel = remember { NfcViewModel(database) }
                NfcScreen(
                    viewModel = viewModel,
                    onNavigateToEditor = { filamentId -> navController.navigate(Routes.editorRoute(filamentId)) },
                )
            }
            composable(Routes.INFO) { InfoScreen() }
        }
    }
}
