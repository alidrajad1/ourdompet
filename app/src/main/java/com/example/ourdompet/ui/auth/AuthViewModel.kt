package com.example.ourdompet.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ourdompet.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    // State untuk UI
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("") // Untuk Register

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(false) // Penanda sukses login

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Cek apakah user sudah login sebelumnya (Auto Login)
    init {
        if (auth.currentUser != null) {
            isLoggedIn = true
        }
    }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email dan Password wajib diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                isLoggedIn = true
            } catch (e: Exception) {
                errorMessage = "Login Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register() {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            errorMessage = "Semua data wajib diisi"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Buat Akun di Auth
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: throw Exception("User ID null")

                // 2. Simpan Data Tambahan (Nama) ke Firestore
                val userData = User(id = userId, name = name, email = email)
                db.collection("users").document(userId).set(userData).await()

                isLoggedIn = true
            } catch (e: Exception) {
                errorMessage = "Register Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        isLoggedIn = false
        email = ""
        password = ""
    }
}