package com.example.ourdompet.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ourdompet.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToAdd: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. Bagian Header Nama & Saldo
            Text("Halo, Selamat Datang", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Kartu Ringkasan (Fitur 1)
            SummaryCard(
                balance = viewModel.currentBalance,
                income = viewModel.totalIncome,
                expense = viewModel.totalExpense
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Riwayat Transaksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // 2. List Transaksi (Fitur 3)
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (viewModel.transactionList.isEmpty()) {
                Text("Belum ada transaksi", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.transactionList) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}

// Komponen Kartu Saldo
@Composable
fun SummaryCard(balance: Double, income: Double, expense: Double) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Sisa Saldo", style = MaterialTheme.typography.labelMedium)
            Text("Rp ${formatRupiah(balance)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Pemasukan", color = Color(0xFF2E7D32)) // Hijau
                    Text("Rp ${formatRupiah(income)}", fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pengeluaran", color = Color(0xFFC62828)) // Merah
                    Text("Rp ${formatRupiah(expense)}", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Komponen Item Transaksi (Baris)
@Composable
fun TransactionItem(transaction: Transaction, onDelete: () -> Unit) {
    val isExpense = transaction.type == "EXPENSE"
    val color = if (isExpense) Color(0xFFC62828) else Color(0xFF2E7D32)
    val prefix = if (isExpense) "-" else "+"

    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.category, fontWeight = FontWeight.Bold)
                Text(
                    text = formatDate(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                if (transaction.note.isNotEmpty()) {
                    Text(transaction.note, style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(
                text = "$prefix Rp ${formatRupiah(transaction.amount)}",
                color = color,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray)
            }
        }
    }
}

// Helper sederhana untuk format angka & tanggal
fun formatRupiah(number: Double): String {
    return String.format(Locale.GERMANY, "%,.0f", number) // Format 10.000
}

fun formatDate(date: java.util.Date?): String {
    if (date == null) return "-"
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(date)
}