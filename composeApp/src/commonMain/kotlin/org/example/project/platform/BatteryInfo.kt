package org.example.project.platform

// ═══════════════════════════════════════════════════════════════════════
// BatteryInfo.kt — commonMain
// expect class: deklarasi API untuk informasi baterai
// Implementasi actual ada di masing-masing platform (androidMain, iosMain)
// Pertemuan 8: expect/actual Pattern — BONUS (+10%)
// ═══════════════════════════════════════════════════════════════════════

expect class BatteryInfo() {
    /** Level baterai dalam persen, rentang 0–100 */
    fun getBatteryLevel(): Int

    /** True jika perangkat sedang diisi daya atau sudah penuh */
    fun isCharging(): Boolean

    /** Deskripsi status: "Charging", "Discharging", "Full", "Unknown" */
    fun getBatteryStatus(): String
}
