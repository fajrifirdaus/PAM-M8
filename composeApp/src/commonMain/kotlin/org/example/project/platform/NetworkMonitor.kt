package org.example.project.platform

import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════
// NetworkMonitor.kt — commonMain
// expect class: deklarasi API untuk pemantauan status jaringan
// Implementasi actual ada di masing-masing platform (androidMain, iosMain)
// Pertemuan 8: expect/actual Pattern
// ═══════════════════════════════════════════════════════════════════════

expect class NetworkMonitor() {
    /** Cek status koneksi secara synchronous (satu kali) */
    fun isConnected(): Boolean

    /** Flow yang mengirim true/false setiap kali status koneksi berubah */
    fun observeConnectivity(): Flow<Boolean>
}
