package com.example.ourdompet.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ourdompet.data.TransactionRepository
import com.example.ourdompet.model.Transaction
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = TransactionRepository()

    // Data yang akan diamati oleh UI
    var transactionList by mutableStateOf<List<Transaction>>(emptyList())
    var totalIncome by mutableStateOf(0.0)
    var totalExpense by mutableStateOf(0.0)
    var currentBalance by mutableStateOf(0.0)
    var isLoading by mutableStateOf(true)

    init {
        fetchTransactions()
    }

    private fun fetchTransactions() {
        viewModelScope.launch {
            // Mengambil data real-time (Flow)
            repository.getTransactions().collect { list ->
                transactionList = list
                calculateSummary(list)
                isLoading = false
            }
        }
    }

    private fun calculateSummary(list: List<Transaction>) {
        // Logika Hitung Saldo (Fitur 1 Dashboard)
        totalIncome = list.filter { it.type == "INCOME" }.sumOf { it.amount }
        totalExpense = list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        currentBalance = totalIncome - totalExpense
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }
}