package com.example.ourdompet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ourdompet.ui.auth.AuthViewModel
import com.example.ourdompet.ui.auth.LoginScreen
import com.example.ourdompet.ui.auth.RegisterScreen
import com.example.ourdompet.ui.theme.OurDompetTheme // Pastikan nama theme sesuai project Anda
import androidx.compose.material3.Text // Untuk dummy dashboard
import com.example.ourdompet.ui.screen.AddTransactionScreen
import com.example.ourdompet.ui.screen.DashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OurDompetTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel() // ViewModel dibagikan

                // Tentukan startDestination berdasarkan status login
                // Catatan: Logika ini sederhana. Idealnya cek di Splash Screen.
                val startDest = if (authViewModel.isLoggedIn) "dashboard" else "login"

                NavHost(navController = navController, startDestination = startDest) {

                    // Halaman Login
                    composable("login") {
                        LoginScreen(
                            viewModel = authViewModel,
                            onNavigateToRegister = { navController.navigate("register") },
                            onLoginSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true } // Hapus history login
                                }
                            }
                        )
                    }

                    // Halaman Register
                    composable("register") {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onNavigateToLogin = { navController.popBackStack() }, // Kembali ke login
                            onRegisterSuccess = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Halaman Dashboard (Sementara)
                    composable("dashboard") {
                        DashboardScreen(
                            onNavigateToAdd = {
                                navController.navigate("add_transaction") // <-- Sambungkan ini
                            }
                        )
                    }
                    // Halaman Tambah Transaksi
                    composable("add_transaction") {
                        AddTransactionScreen(
                            onBack = {
                                navController.popBackStack() // Kembali ke dashboard
                            }
                        )
                    }
                }
            }
        }
    }
}