package org.example.project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.NoteCategory
import org.example.project.model.NoteColor
import org.example.project.viewmodel.NotesViewModel

// ═══════════════════════════════════════════════════
// EDIT NOTE SCREEN — Menerima noteId sebagai argument
// Menampilkan data note yang sudah ada untuk diedit
// ═══════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,                   // ← ARGUMENT dari NavHost
    viewModel: NotesViewModel,
    onBack: () -> Unit
) {
    val note = viewModel.getNoteById(noteId)

    // Jika note tidak ditemukan, fallback UI
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

    // State diisi dari data note yang sudah ada (pre-filled)
    var title by remember(note.id) { mutableStateOf(note.title) }
    var content by remember(note.id) { mutableStateOf(note.content) }
    var selectedCategory by remember(note.id) { mutableStateOf(note.category) }
    var selectedColor by remember(note.id) { mutableStateOf(note.color) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    Scaffold(
        // ✅ FIX: Set contentWindowInsets ke zero agar tidak terjadi double padding
        // karena parent Scaffold di AppNavigation sudah menangani system insets
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    // ✅ Single title tanpa subtitle agar tinggi bar tetap standar
                    Text("Edit Catatan", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                            } else {
                                viewModel.updateNote(noteId, title, content, selectedCategory, selectedColor)
                                onBack()
                            }
                        }
                    ) {
                        Text(
                            "Simpan",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                    }
                },
                // ✅ FIX: windowInsets TopAppBar juga di-zero agar tidak double
                windowInsets = WindowInsets(0.dp),
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title input (pre-filled)
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Judul Catatan") },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Judul tidak boleh kosong", color = MaterialTheme.colorScheme.error) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Content input (pre-filled)
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi Catatan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = RoundedCornerShape(14.dp),
                minLines = 5
            )

            // Category selector
            Column {
                Text(
                    "Kategori",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        NoteCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.label) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Color selector
            Column {
                Text(
                    "Warna Catatan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NoteColor.entries.forEach { color ->
                        ColorCircle(
                            color = androidx.compose.ui.graphics.Color(color.lightHex),
                            isSelected = selectedColor == color,
                            onClick = { selectedColor = color }
                        )
                    }
                }
            }

            // Update button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        viewModel.updateNote(noteId, title, content, selectedCategory, selectedColor)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.Update, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Perbarui Catatan", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}