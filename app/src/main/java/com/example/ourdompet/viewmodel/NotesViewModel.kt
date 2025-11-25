package com.example.ourdompet.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ourdompet.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        fetchNotes()
    }

    // Mengambil data secara Realtime
    private fun fetchNotes() {
        db.collection("catatan")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                val noteList = snapshot?.documents?.map { doc ->
                    Note(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: ""
                    )
                } ?: emptyList()
                _notes.value = noteList
            }
    }

    // Tambah Catatan
    fun addNote(title: String, content: String, onSuccess: () -> Unit) {
        val note = hashMapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("catatan").add(note).addOnSuccessListener { onSuccess() }
    }

    // Update Catatan
    fun updateNote(id: String, title: String, content: String, onSuccess: () -> Unit) {
        val note = mapOf("title" to title, "content" to content)
        db.collection("catatan").document(id).update(note).addOnSuccessListener { onSuccess() }
    }

    // Hapus Catatan
    fun deleteNote(id: String) {
        db.collection("catatan").document(id).delete()
    }
}