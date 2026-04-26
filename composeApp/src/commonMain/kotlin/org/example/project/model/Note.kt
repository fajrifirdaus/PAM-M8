package org.example.project.model

import androidx.compose.ui.graphics.Color

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val category: NoteCategory = NoteCategory.PERSONAL,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val color: NoteColor = NoteColor.DEFAULT
)

enum class NoteCategory(val label: String) {
    PERSONAL("Personal"),
    WORK("Work"),
    IDEAS("Ideas"),
    IMPORTANT("Important")
}

// Setiap warna punya versi terang (light mode) dan gelap (dark mode)
enum class NoteColor(
    val lightHex: Long,   // pastel terang untuk light mode
    val darkHex: Long     // versi gelap untuk dark mode
) {
    DEFAULT(lightHex = 0xFFFFF8F0, darkHex = 0xFF3D2E1E),  // krem → coklat tua
    YELLOW( lightHex = 0xFFFFF9C4, darkHex = 0xFF3D3000),  // kuning → kuning gelap
    GREEN(  lightHex = 0xFFE8F5E9, darkHex = 0xFF0D3320),  // hijau  → hijau hutan
    BLUE(   lightHex = 0xFFE3F2FD, darkHex = 0xFF0D2A3D),  // biru   → biru navy
    PINK(   lightHex = 0xFFFCE4EC, darkHex = 0xFF3D0D1F),  // pink   → merah marun
    PURPLE( lightHex = 0xFFF3E5F5, darkHex = 0xFF2A0D3D)   // ungu   → ungu tua
}

// Helper: ambil warna yang sesuai dengan mode
fun NoteColor.toColor(isDarkMode: Boolean): Color =
    if (isDarkMode) Color(darkHex) else Color(lightHex)
