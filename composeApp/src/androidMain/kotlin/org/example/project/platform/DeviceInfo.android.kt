package org.example.project.platform

import android.content.res.Resources
import android.os.Build
import kotlin.math.pow
import kotlin.math.sqrt

// ═══════════════════════════════════════════════════════════════════════
// DeviceInfo.android.kt — androidMain
// actual class untuk informasi perangkat Android
// Pertemuan 8: expect/actual Pattern
// ═══════════════════════════════════════════════════════════════════════

actual class DeviceInfo {

    /** Model perangkat, contoh: "Pixel 7 Pro", "Samsung Galaxy S23" */
    actual fun getDeviceName(): String = Build.MODEL

    /** Versi OS lengkap, contoh: "Android 14 (API 34)" */
    actual fun getOsVersion(): String =
        "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    /** Versi aplikasi — hardcoded karena PackageManager butuh Context */
    actual fun getAppVersion(): String = "1.0.0"

    /** Produsen perangkat, contoh: "Google", "Samsung", "Xiaomi" */
    actual fun getManufacturer(): String =
        Build.MANUFACTURER.replaceFirstChar { it.uppercase() }

    /**
     * Mendeteksi tablet berdasarkan diagonal layar fisik.
     * Perangkat dengan diagonal >= 7 inci dianggap tablet.
     */
    actual fun isTablet(): Boolean {
        val metrics    = Resources.getSystem().displayMetrics
        val widthInch  = metrics.widthPixels  / metrics.xdpi
        val heightInch = metrics.heightPixels / metrics.ydpi
        val diagonal   = sqrt(widthInch.pow(2) + heightInch.pow(2))
        return diagonal >= 7.0f
    }
}
