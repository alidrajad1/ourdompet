package com.example.ourdompet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.example.ourdompet.ui.auth.AuthViewModel
import com.example.ourdompet.ui.auth.LoginScreen
import com.example.ourdompet.ui.auth.RegisterScreen
import com.example.ourdompet.ui.screen.AddTransactionScreen
import com.example.ourdompet.ui.screen.DashboardScreen
import com.example.ourdompet.ui.screen.NotesListScreen
import com.example.ourdompet.ui.screen.AddEditNoteScreen
import com.example.ourdompet.ui.theme.OurDompetTheme
import com.example.ourdompet.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OurDompetTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val notesViewModel: NotesViewModel = viewModel()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val showBottomBar = currentDestination?.route in listOf("dashboard", "notes")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
                                    onClick = {
                                        navController.navigate("dashboard") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Edit, contentDescription = "Catatan") },
                                    label = { Text("Catatan") },
                                    selected = currentDestination?.hierarchy?.any { it.route == "notes" } == true,
                                    onClick = {
                                        navController.navigate("notes") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (authViewModel.isLoggedIn) "dashboard" else "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // --- AUTH (Tanpa animasi slide khusus) ---
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = { navController.navigate("register") },
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- DASHBOARD (HOME) ---
                        composable(
                            route = "dashboard",
                            // Jika masuk dari 'notes', slide masuk dari KIRI
                            enterTransition = {
                                if (initialState.destination.route == "notes") {
                                    slideInHorizontally(initialOffsetX = { -it })
                                } else null
                            },
                            // Jika keluar ke 'notes', slide keluar ke KIRI
                            exitTransition = {
                                if (targetState.destination.route == "notes") {
                                    slideOutHorizontally(targetOffsetX = { -it })
                                } else null
                            }
                        ) {
                            DashboardScreen(
                                onNavigateToAdd = { navController.navigate("add_transaction") }
                            )
                        }

                        // --- ADD TRANSACTION ---
                        composable("add_transaction") {
                            AddTransactionScreen(onBack = { navController.popBackStack() })
                        }

                        // --- FITUR CATATAN (NOTES) ---
                        composable(
                            route = "notes",
                            // Jika masuk dari 'dashboard', slide masuk dari KANAN
                            enterTransition = {
                                if (initialState.destination.route == "dashboard") {
                                    slideInHorizontally(initialOffsetX = { it })
                                } else null
                            },
                            // Jika keluar ke 'dashboard', slide keluar ke KANAN
                            exitTransition = {
                                if (targetState.destination.route == "dashboard") {
                                    slideOutHorizontally(targetOffsetX = { it })
                                } else null
                            }
                        ) {
                            NotesListScreen(
                                viewModel = notesViewModel,
                                navController = navController
                            )
                        }

                        // --- DETAIL CATATAN ---
                        composable(
                            route = "add_edit_note?noteId={noteId}",
                            arguments = listOf(navArgument("noteId") {
                                nullable = true
                                defaultValue = null
                                type = NavType.StringType
                            }),
                            // Animasi Slide Masuk dari bawah (opsional, biar keren saat edit)
                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")
                            AddEditNoteScreen(
                                viewModel = notesViewModel,
                                navController = navController,
                                noteId = noteId
                            )
                        }
                    }
                }
            }
        }
    }
}