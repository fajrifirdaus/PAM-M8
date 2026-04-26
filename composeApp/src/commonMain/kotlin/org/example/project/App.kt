package org.example.project

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.navigation.AppNavigation
import org.example.project.ui.theme.NotesAppTheme
import org.example.project.viewmodel.NotesViewModel
import org.koin.compose.viewmodel.koinViewModel

// ═══════════════════════════════════════════════════════════════════════
// App.kt — commonMain
// Entry point Compose Multiplatform
//
// Pertemuan 8: Parameter dihapus — semua dependency di-inject oleh Koin.
// Tidak ada lagi DatabaseDriverFactory / SettingsFactory manual.
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun App() {
    // koinViewModel() meminta Koin untuk menyediakan NotesViewModel
    // beserta semua dependencies-nya (NoteRepository, SettingsManager,
    // DeviceInfo, NetworkMonitor, BatteryInfo)
    val notesViewModel: NotesViewModel = koinViewModel()
    val uiState by notesViewModel.uiState.collectAsStateWithLifecycle()

    NotesAppTheme(darkTheme = uiState.isDarkMode) {
        AppNavigation(notesViewModel = notesViewModel)
    }
}
