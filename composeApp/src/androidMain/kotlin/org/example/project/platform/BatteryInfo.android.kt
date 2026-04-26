package org.example.project.platform

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

// ═══════════════════════════════════════════════════════════════════════
// BatteryInfo.android.kt — androidMain
// actual class untuk informasi baterai Android
// Pertemuan 8: expect/actual Pattern — BONUS (+10%)
// ═══════════════════════════════════════════════════════════════════════

actual class BatteryInfo {

    private val ctx: Context
        get() = AndroidBatteryHelper.appContext
            ?: error("AndroidBatteryHelper belum diinisialisasi. Panggil AndroidBatteryHelper.init() di MyApplication.")

    /** Level baterai dalam persen (0–100) */
    actual fun getBatteryLevel(): Int {
        val bm = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
    }

    /** True jika sedang charging atau penuh */
    actual fun isCharging(): Boolean {
        val intent = batteryIntent() ?: return false
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
               status == BatteryManager.BATTERY_STATUS_FULL
    }

    /** Status baterai sebagai teks deskriptif */
    actual fun getBatteryStatus(): String {
        val intent = batteryIntent() ?: return "Unknown"
        return when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING     -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING  -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL         -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else                                        -> "Unknown"
        }
    }

    private fun batteryIntent(): Intent? =
        ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
}

/**
 * Singleton helper untuk menyimpan Application Context.
 * Diinisialisasi dari MyApplication.onCreate().
 */
object AndroidBatteryHelper {
    var appContext: Context? = null
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }
}
