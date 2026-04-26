package org.example.project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.EmptyStateView
import org.example.project.components.NoteCard
import org.example.project.viewmodel.NotesViewModel

// ═══════════════════════════════════════════════════
// FAVORITES SCREEN — Tab kedua, catatan favorit
// ═══════════════════════════════════════════════════

@Composable
fun FavoritesScreen(
    viewModel: NotesViewModel,
    isDarkMode: Boolean,
    onNoteClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites = uiState.favoriteNotes

    // Delete dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text("Hapus Catatan", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah kamu yakin ingin menghapus catatan ini?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteNote() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteDialog() }) {
                    Text("Batal")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp,
            top = 16.dp, bottom = 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${favorites.size} Catatan Favorit",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(4.dp))
        }

        if (favorites.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Belum ada favorit",
                    subtitle = "Tandai catatan sebagai favorit dengan ikon bookmark"
                )
            }
        } else {
            items(favorites, key = { it.id }) { note ->
                NoteCard(
                    note = note,
                    isDarkMode = isDarkMode,
                    onClick = { onNoteClick(note.id) },
                    onFavoriteToggle = { viewModel.toggleFavorite(note.id, note.isFavorite) },
                    onDeleteRequest = { viewModel.requestDeleteNote(note.id) }
                )
            }
        }
    }
}
