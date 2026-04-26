package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.Note
import org.example.project.model.toColor
import org.example.project.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int,
    viewModel: NotesViewModel,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val note: Note? = viewModel.getNoteById(noteId)

    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text("Catatan tidak ditemukan", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onBack) { Text("Kembali") }
            }
        }
        return
    }

    val noteColor = note.color.toColor(isDarkMode)
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy • HH:mm", Locale("id", "ID"))
    val dateStr = dateFormat.format(Date(note.createdAt))

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp), // ✅ FIX
        topBar = {
            TopAppBar(
                title = { Text("Detail Catatan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(noteId, note.isFavorite) }) {
                        Icon(
                            imageVector = if (note.isFavorite) Icons.Rounded.Favorite
                            else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (note.isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { onEdit(noteId) }) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = "Edit Catatan",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp), // ✅ FIX
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(noteColor)
                    .padding(24.dp)
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = androidx.compose.ui.graphics.Color(0x220061FF)
                    ) {
                        Text(
                            text = note.category.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = androidx.compose.ui.graphics.Color(0xFF0045B5),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = note.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDarkMode) Color.White else Color(0xFF1A1A2E),
                        lineHeight = 32.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isDarkMode) Color.White.copy(alpha = 0.55f) else Color(0xFF7A7A9A)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            color = if (isDarkMode) Color.White.copy(alpha = 0.55f) else Color(0xFF7A7A9A)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = note.content.ifEmpty { "Tidak ada konten." },
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onEdit(noteId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Catatan", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}