package com.example.ourdompet.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ourdompet.model.Note
import com.example.ourdompet.viewmodel.NotesViewModel

// --- Halaman Utama List Catatan ---
@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk TopAppBar
@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    navController: NavController
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        // Bagian Judul di Atas (Top Bar)
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catatan Keuangan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // Warna latar header
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer // Warna teks header
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_edit_note") }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
            }
        }
    ) { padding ->
        // Konten List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tampilkan pesan jika kosong
            if (notes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada catatan.", color = Color.Gray)
                    }
                }
            }

            items(notes) { note ->
                NoteItem(note = note, onClick = {
                    // Navigasi ke edit dengan membawa ID
                    navController.navigate("add_edit_note?noteId=${note.id}")
                }, onDelete = {
                    viewModel.deleteNote(note.id)
                })
            }
        }
    }
}

// --- Item Tampilan Per Catatan ---
@Composable
fun NoteItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = note.content, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// --- Halaman Tambah / Edit Catatan ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: NotesViewModel,
    navController: NavController,
    noteId: String? = null // Jika null berarti Tambah, jika ada berarti Edit
) {
    val notes by viewModel.notes.collectAsState()
    val existingNote = notes.find { it.id == noteId }

    var title by remember { mutableStateOf(existingNote?.title ?: "") }
    var content by remember { mutableStateOf(existingNote?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "Buat Catatan" else "Edit Catatan") },
                navigationIcon = {
                    // Tombol kembali (opsional, tapi bagus untuk UX)
                    // Bisa ditambahkan IconButton di sini jika perlu
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Penting agar tidak tertutup header
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi Catatan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = 20
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && content.isNotEmpty()) {
                        if (noteId == null) {
                            viewModel.addNote(title, content) { navController.popBackStack() }
                        } else {
                            viewModel.updateNote(noteId, title, content) { navController.popBackStack() }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan")
            }
        }
    }
}