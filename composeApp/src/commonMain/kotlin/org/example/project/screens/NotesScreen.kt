package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.EmptyStateView
import org.example.project.components.NetworkStatusBanner
import org.example.project.components.NoteCard
import org.example.project.viewmodel.NotesViewModel

// ═══════════════════════════════════════════════════════════════════════
// NotesScreen.kt — commonMain
// Pertemuan 8: Ditambahkan NetworkStatusBanner di atas search bar
// ═══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel  : NotesViewModel,
    isDarkMode : Boolean,
    onNoteClick: (Int) -> Unit,
    onAddNote  : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val notes    = uiState.notes

    // ── Delete Confirmation Dialog ──────────────────────────────────────
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title   = { Text("Hapus Catatan", fontWeight = FontWeight.Bold) },
            text    = { Text("Apakah kamu yakin ingin menghapus catatan ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteNote() },
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteDialog() }) { Text("Batal") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Network Status Banner (Pertemuan 8) ───────────────────
            // Muncul otomatis saat offline, hilang saat kembali online
            NetworkStatusBanner(
                isConnected = uiState.isConnected,
                modifier    = Modifier.fillMaxWidth()
            )

            // ── Search Bar ────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder   = { Text("Cari catatan...") },
                leadingIcon   = {
                    Icon(Icons.Rounded.Search, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon  = {
                    AnimatedVisibility(
                        visible = uiState.searchQuery.isNotEmpty(),
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape      = RoundedCornerShape(16.dp),
                singleLine = true
            )

            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Count info row
                item {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = "${notes.size} Catatan",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        if (uiState.searchQuery.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text       = "Filter aktif",
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.primary,
                                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (notes.isEmpty()) {
                    item {
                        EmptyStateView(
                            title    = if (uiState.searchQuery.isNotEmpty()) "Tidak ditemukan"
                                       else "Belum ada catatan",
                            subtitle = if (uiState.searchQuery.isNotEmpty()) "Coba kata kunci lain"
                                       else "Tap tombol + untuk menambah catatan baru"
                        )
                    }
                } else {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note             = note,
                            isDarkMode       = isDarkMode,
                            onClick          = { onNoteClick(note.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(note.id, note.isFavorite) },
                            onDeleteRequest  = { viewModel.requestDeleteNote(note.id) }
                        )
                    }
                }
            }
        }

        // ── FAB ───────────────────────────────────────────────────────
        FloatingActionButton(
            onClick        = onAddNote,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = MaterialTheme.colorScheme.onPrimary,
            shape          = RoundedCornerShape(16.dp),
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Tambah Catatan")
                Spacer(Modifier.width(8.dp))
                Text("Catatan Baru", fontWeight = FontWeight.Bold)
            }
        }
    }
}
