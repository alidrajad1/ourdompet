package com.example.ourdompet.data

import android.util.Log
import com.example.ourdompet.model.Transaction
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TransactionRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // 1. Fungsi Menambah Transaksi
    suspend fun addTransaction(transaction: Transaction): Boolean {
        return try {
            val currentUser = auth.currentUser ?: return false

            // Simpan ke: users -> {userId} -> transactions -> {dokumenBaru}
            val transactionData = transaction.copy(userId = currentUser.uid)

            db.collection("users")
                .document(currentUser.uid)
                .collection("transactions")
                .add(transactionData)
                .await()

            true // Berhasil
        } catch (e: Exception) {
            Log.e("Repo", "Error add transaction", e)
            false // Gagal
        }
    }

    // 2. Fungsi Mengambil Data Realtime (Flow)
    fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            close() // Jika belum login, tutup aliran data
            return@callbackFlow
        }

        val subscription = db.collection("users")
            .document(currentUser.uid)
            .collection("transactions")
            .orderBy("date", Query.Direction.DESCENDING) // Urutkan dari yang terbaru
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Jika error, tutup stream
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Ubah dokumen Firestore menjadi List<Transaction>
                    val transactions = snapshot.toObjects(Transaction::class.java)
                    trySend(transactions) // Kirim data ke UI
                }
            }

        // Membersihkan listener saat UI tidak lagi butuh data
        awaitClose { subscription.remove() }
    }

    // 3. Fungsi Hapus Transaksi
    suspend fun deleteTransaction(transactionId: String) {
        val currentUser = auth.currentUser ?: return
        try {
            db.collection("users")
                .document(currentUser.uid)
                .collection("transactions")
                .document(transactionId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("Repo", "Error delete", e)
        }
    }

    // 4. Fungsi Update/Edit Transaksi
    suspend fun updateTransaction(transaction: Transaction) {
        val currentUser = auth.currentUser ?: return
        try {
            db.collection("users")
                .document(currentUser.uid)
                .collection("transactions")
                .document(transaction.id) // ID dokumen yang mau diedit
                .set(transaction) // Timpa data lama dengan yang baru
                .await()
        } catch (e: Exception) {
            Log.e("Repo", "Error update", e)
        }
    }
}