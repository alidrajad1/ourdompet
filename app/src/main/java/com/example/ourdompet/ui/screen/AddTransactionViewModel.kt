package com.example.ourdompet.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ourdompet.data.TransactionRepository
import com.example.ourdompet.model.Transaction
import kotlinx.coroutines.launch

class AddTransactionViewModel : ViewModel() {
    private val repository = TransactionRepository()

    // State Form Input
    var amount by mutableStateOf("")
    var category by mutableStateOf("")
    var note by mutableStateOf("")
    var type by mutableStateOf("EXPENSE") // Default: Pengeluaran

    var isLoading by mutableStateOf(false)

    fun saveTransaction(onSuccess: () -> Unit) {
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || category.isBlank()) {
            return // Validasi sederhana: Jangan simpan jika kosong
        }

        viewModelScope.launch {
            isLoading = true
            val newTransaction = Transaction(
                type = type,
                amount = amountDouble,
                category = category,
                note = note
            )

            val isSuccess = repository.addTransaction(newTransaction)
            isLoading = false

            if (isSuccess) {
                onSuccess() // Beritahu UI untuk kembali ke Dashboard
            }
        }
    }
}