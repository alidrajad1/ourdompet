package com.example.ourdompet.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaction(
    @DocumentId
    val id: String = "", // ID unik dokumen Firestore akan otomatis masuk sini

    val userId: String = "", // ID user pemilik transaksi
    val type: String = "",   // "INCOME" atau "EXPENSE"
    val amount: Double = 0.0,
    val category: String = "",
    val note: String = "",

    @ServerTimestamp
    val date: Date? = null   // Tanggal otomatis dari server
)