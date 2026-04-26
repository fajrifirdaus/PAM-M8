package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.data.repository.NoteRepository
import org.example.project.data.repository.SettingsManager
import org.example.project.model.Note
import org.example.project.model.NoteCategory
import org.example.project.model.NoteColor
import org.example.project.platform.BatteryInfo
import org.example.project.platform.DeviceInfo
import org.example.project.platform.NetworkMonitor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect

// ═══════════════════════════════════════════════════════════════════════
// NotesViewModel.kt — commonMain
// Pertemuan 8:
//   - DeviceInfo    → info perangkat (one-shot, tidak berubah)
//   - NetworkMonitor → status koneksi real-time via Flow
//   - BatteryInfo   → polling setiap 30 detik (realtime)
// ═══════════════════════════════════════════════════════════════════════

data class NotesUiState(
    // ── Notes ─────────────────────────────────────────────────────────
    val notes            : List<Note> = emptyList(),
    val favoriteNotes    : List<Note> = emptyList(),
    val searchQuery      : String     = "",
    val isDarkMode       : Boolean    = false,
    val showDeleteDialog : Boolean    = false,
    val noteToDelete     : Int?       = null,
    val snackbarMessage  : String?    = null,

    // ── Network ───────────────────────────────────────────────────────
    val isConnected      : Boolean    = true,

    // ── Device Info ───────────────────────────────────────────────────
    val deviceName       : String     = "",
    val osVersion        : String     = "",
    val appVersion       : String     = "",
    val manufacturer     : String     = "",
    val isTablet         : Boolean    = false,

    // ── Battery (realtime polling) ────────────────────────────────────
    val batteryLevel     : Int        = -1,
    val isCharging       : Boolean    = false,
    val batteryStatus    : String     = "Unknown"
)

/** Interval polling baterai dalam milidetik */
private const val BATTERY_POLL_INTERVAL_MS = 1_000L

class NotesViewModel(
    private val repository      : NoteRepository,
    private val settingsManager : SettingsManager,
    private val deviceInfo      : DeviceInfo,
    private val networkMonitor  : NetworkMonitor,
    private val batteryInfo     : BatteryInfo
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        observeDarkMode()
        observeNotes()
        observeNetworkStatus()
        loadDeviceInfo()
        startBatteryPolling()   // ← realtime polling setiap 30 detik
    }

    // ── Dark Mode ──────────────────────────────────────────────────────

    private fun observeDarkMode() {
        viewModelScope.launch {
            settingsManager.isDarkModeFlow.collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsManager.toggleDarkMode(!_uiState.value.isDarkMode)
        }
    }

    // ── Notes ──────────────────────────────────────────────────────────

    private fun observeNotes() {
        viewModelScope.launch {
            _uiState.map { it.searchQuery }.distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isEmpty()) repository.getAllNotes()
                    else repository.searchNotes(query)
                }
                .collect { notes -> _uiState.update { it.copy(notes = notes) } }
        }
        viewModelScope.launch {
            repository.getFavoriteNotes().collect { favs ->
                _uiState.update { it.copy(favoriteNotes = favs) }
            }
        }
    }

    var currentViewedNote: Note? = null
        private set

    fun loadNoteById(id: Int, onLoaded: (Note?) -> Unit) {
        viewModelScope.launch {
            currentViewedNote = repository.getNoteById(id)
            onLoaded(currentViewedNote)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFavorite(noteId: Int, isFavorite: Boolean) {
        viewModelScope.launch { repository.toggleFavorite(noteId, isFavorite) }
    }

    fun addNote(title: String, content: String, category: NoteCategory, color: NoteColor) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertNote(title, content, category, color)
            _uiState.update { it.copy(snackbarMessage = "Catatan disimpan") }
        }
    }

    fun updateNote(
        id: Int, title: String, content: String,
        category: NoteCategory, color: NoteColor, isFav: Boolean = false
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.updateNote(id, title, content, category, isFav, color)
            _uiState.update { it.copy(snackbarMessage = "Catatan diperbarui") }
        }
    }

    fun requestDeleteNote(noteId: Int) {
        _uiState.update { it.copy(showDeleteDialog = true, noteToDelete = noteId) }
    }

    fun confirmDeleteNote() {
        val id = _uiState.value.noteToDelete ?: return
        viewModelScope.launch {
            repository.deleteNote(id)
            _uiState.update {
                it.copy(showDeleteDialog = false, noteToDelete = null,
                    snackbarMessage = "Catatan dihapus")
            }
        }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, noteToDelete = null) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun getNoteById(id: Int): Note? = _uiState.value.notes.find { it.id == id }

    // ── Network Status (real-time via Flow) ────────────────────────────

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.observeConnectivity().collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
        }
    }

    // ── Device Info (one-shot, tidak berubah saat runtime) ─────────────

    private fun loadDeviceInfo() {
        _uiState.update {
            it.copy(
                deviceName   = deviceInfo.getDeviceName(),
                osVersion    = deviceInfo.getOsVersion(),
                appVersion   = deviceInfo.getAppVersion(),
                manufacturer = deviceInfo.getManufacturer(),
                isTablet     = deviceInfo.isTablet()
            )
        }
    }

    // ── Battery Info (realtime polling setiap 30 detik) ────────────────

    private fun startBatteryPolling() {
        viewModelScope.launch {
            // Buat Flow yang emit setiap BATTERY_POLL_INTERVAL_MS
            // menggunakan ticker pattern dengan delay loop
            while (true) {
                val level    = batteryInfo.getBatteryLevel()
                val charging = batteryInfo.isCharging()
                val status   = batteryInfo.getBatteryStatus()
                _uiState.update {
                    it.copy(
                        batteryLevel  = level,
                        isCharging    = charging,
                        batteryStatus = status
                    )
                }
                delay(BATTERY_POLL_INTERVAL_MS)
            }
        }
    }

    /** Paksa refresh baterai sekarang (dipanggil dari SettingsScreen saat screen aktif) */
    fun refreshBatteryInfo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    batteryLevel  = batteryInfo.getBatteryLevel(),
                    isCharging    = batteryInfo.isCharging(),
                    batteryStatus = batteryInfo.getBatteryStatus()
                )
            }
        }
    }
}
