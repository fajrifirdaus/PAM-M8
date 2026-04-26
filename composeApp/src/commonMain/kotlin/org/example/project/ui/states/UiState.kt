package org.example.project.ui.states

// ═══════════════════════════════════════════════════
// UI STATE — Sealed class untuk mengelola semua state
// Loading | Success | Error
// Sesuai materi Pertemuan 6: Loading dan Error States
// ═══════════════════════════════════════════════════

sealed class UiState<out T> {

    /** State saat data sedang dimuat dari API / cache */
    data object Loading : UiState<Nothing>()

    /** State saat data berhasil dimuat */
    data class Success<T>(val data: T) : UiState<T>()

    /** State saat terjadi error (network, parsing, dsb) */
    data class Error(val message: String) : UiState<Nothing>()
}

// Extension helpers
fun <T> UiState<T>.isLoading() = this is UiState.Loading
fun <T> UiState<T>.isSuccess() = this is UiState.Success
fun <T> UiState<T>.isError()   = this is UiState.Error
fun <T> UiState<T>.getOrNull() = (this as? UiState.Success)?.data
