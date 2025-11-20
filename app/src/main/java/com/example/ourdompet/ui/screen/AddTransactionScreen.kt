package com.example.ourdompet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = viewModel(),
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. Pilihan Jenis (Pemasukan / Pengeluaran)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TypeButton(
                    text = "Pengeluaran",
                    isSelected = viewModel.type == "EXPENSE",
                    color = Color(0xFFC62828), // Merah
                    onClick = { viewModel.type = "EXPENSE" },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    text = "Pemasukan",
                    isSelected = viewModel.type == "INCOME",
                    color = Color(0xFF2E7D32), // Hijau
                    onClick = { viewModel.type = "INCOME" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Input Nominal
            OutlinedTextField(
                value = viewModel.amount,
                onValueChange = { viewModel.amount = it },
                label = { Text("Nominal (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Input Kategori
            OutlinedTextField(
                value = viewModel.category,
                onValueChange = { viewModel.category = it },
                label = { Text("Kategori (Misal: Makan, Gaji)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Input Catatan
            OutlinedTextField(
                value = viewModel.note,
                onValueChange = { viewModel.note = it },
                label = { Text("Catatan (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Tombol Simpan
            Button(
                onClick = {
                    viewModel.saveTransaction(onSuccess = onBack)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Transaksi")
                }
            }
        }
    }
}

// Komponen Tombol Pilihan Custom agar cantik
@Composable
fun TypeButton(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        modifier = modifier
    ) {
        Text(text)
    }
}