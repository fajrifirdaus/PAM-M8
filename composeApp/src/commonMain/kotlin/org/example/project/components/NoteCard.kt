package org.example.project.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.Note
import org.example.project.model.toColor
import org.example.project.ui.theme.AzureBlue
import org.example.project.ui.theme.AzureLight
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════
// NOTE CARD COMPONENT
// ─────────────────────────────────────────────────
// Logika warna:
//   Light mode → background pastel terang, teks gelap
//   Dark mode  → background pastel digelapkan, teks putih
//   Chip kategori → SELALU biru (tidak ikut logika di atas)
// ═══════════════════════════════════════════════════

@Composable
fun NoteCard(
    note: Note,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pilih warna background sesuai mode
    val bgColor = note.color.toColor(isDarkMode)

    // Warna teks: putih di dark mode, gelap di light mode
    val textPrimary   = if (isDarkMode) Color.White                    else Color(0xFF1A1A2E)
    val textSecondary = if (isDarkMode) Color.White.copy(alpha = 0.75f) else Color(0xFF3D3D5C)
    val textTertiary  = if (isDarkMode) Color.White.copy(alpha = 0.45f) else Color(0xFF7A7A9A)

    // Chip kategori — solid terang di dark mode agar kontras di atas card gelap
    //                 transparan tipis di light mode karena card sudah terang
    val chipBg   = if (isDarkMode) Color(0xFF60A5FA) else Color(0x330061FF)
    val chipText = if (isDarkMode) Color(0xFF0D1B2A) else Color(0xFF003D99)

    // Warna ikon Favorite sesuai mode
    val FavoriteTint = if (isDarkMode) AzureLight else AzureBlue

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val dateStr = dateFormat.format(Date(note.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header: chip kategori + tombol aksi ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip kategori — SELALU biru, tidak ikut dark/light
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = chipBg
                ) {
                    Text(
                        text = note.category.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = chipText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row {
                    // Tombol Favorite
                    IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (note.isFavorite) Icons.Rounded.Favorite
                                          else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (note.isFavorite) FavoriteTint
                                   else textTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Tombol hapus
                    IconButton(onClick = onDeleteRequest, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Delete",
                            tint = if (isDarkMode) Color(0xFFFF6B6B)
                                   else Color(0xFFD32F2F).copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Judul ──
            Text(
                text = note.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // ── Isi preview ──
            Text(
                text = note.content,
                fontSize = 13.sp,
                color = textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(10.dp))

            // ── Tanggal ──
            Text(
                text = dateStr,
                fontSize = 11.sp,
                color = textTertiary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// EMPTY STATE VIEW
// ═══════════════════════════════════════════════════

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.NoteAlt,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
        )
    }
}
