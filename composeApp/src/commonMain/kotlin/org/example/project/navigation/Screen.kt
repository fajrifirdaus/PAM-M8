package org.example.project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

// ═══════════════════════════════════════════════════
// CENTRALIZED ROUTES — Minggu 5 + Minggu 6
// ═══════════════════════════════════════════════════

sealed class Screen(val route: String) {

    // ── Minggu 5: Notes App ──
    object Notes     : Screen("notes")
    object Favorites : Screen("favorites")
    object Profile   : Screen("profile")
    object AddNote   : Screen("add_note")
    object Settings  : Screen("settings")
    object About     : Screen("about")

    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }

    // ── Minggu 6: News Reader ──
    object NewsList   : Screen("news_list")
    object NewsDetail : Screen("news_detail/{articleId}") {
        fun createRoute(articleId: Int) = "news_detail/$articleId"
    }
}

// ═══════════════════════════════════════════════════
// BOTTOM NAVIGATION ITEMS — Updated dengan News tab
// ═══════════════════════════════════════════════════

sealed class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
) {
    object News      : BottomNavItem(Screen.NewsList,  Icons.Rounded.Newspaper,      "Berita")
    object Notes     : BottomNavItem(Screen.Notes,     Icons.Rounded.EditNote,           "Catatan")
    object Favorites : BottomNavItem(Screen.Favorites, Icons.Rounded.FavoriteBorder, "Favorit")
    object Profile   : BottomNavItem(Screen.Profile,   Icons.Rounded.Person,         "Profil")
}
