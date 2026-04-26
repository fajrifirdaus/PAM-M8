package org.example.project

import android.app.Application
import org.example.project.di.androidPlatformModule
import org.example.project.di.appModule
import org.example.project.platform.AndroidBatteryHelper
import org.example.project.platform.AndroidNetworkMonitorHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

// ═══════════════════════════════════════════════════════════════════════
// MyApplication.kt — androidMain
// Application class: inisialisasi Koin DI sebelum Activity apapun dibuat
// Pertemuan 8: Dependency Injection dengan Koin
// ═══════════════════════════════════════════════════════════════════════

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Init Context helpers SEBELUM startKoin agar NetworkMonitor & BatteryInfo
        // langsung bisa pakai Context saat pertama kali dipakai
        AndroidNetworkMonitorHelper.init(this)
        AndroidBatteryHelper.init(this)

        startKoin {
            androidLogger(Level.DEBUG)          // Ganti Level.ERROR untuk production
            androidContext(this@MyApplication)
            modules(
                androidPlatformModule(),        // DatabaseDriverFactory(ctx), SettingsFactory(ctx)
                appModule                       // DB, Settings, Networking, Repos, ViewModels, DeviceInfo, Network, Battery
            )
        }
    }
}
