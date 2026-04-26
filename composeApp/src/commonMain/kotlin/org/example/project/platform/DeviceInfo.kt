package org.example.project.platform

// ═══════════════════════════════════════════════════════════════════════
// DeviceInfo.kt — commonMain
// expect class: deklarasi API untuk informasi perangkat
// Implementasi actual ada di masing-masing platform (androidMain, iosMain)
// Pertemuan 8: expect/actual Pattern
// ═══════════════════════════════════════════════════════════════════════

expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
    fun getManufacturer(): String
    fun isTablet(): Boolean
}
