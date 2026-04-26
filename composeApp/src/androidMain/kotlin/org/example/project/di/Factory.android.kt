package org.example.project.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.example.project.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

// ═══════════════════════════════════════════════════════════════════════
// Factory.android.kt — androidMain
// actual implementations untuk expect class di Factory.kt (commonMain)
// Pertemuan 7: Local Storage  |  Pertemuan 8: Koin androidPlatformModule
// ═══════════════════════════════════════════════════════════════════════

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
}

actual class SettingsFactory(private val context: Context) {
    actual fun createSettings(): ObservableSettings =
        SharedPreferencesSettings(
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        )
}

/**
 * Koin module khusus Android yang menyediakan DatabaseDriverFactory
 * dan SettingsFactory dengan androidContext().
 * Di-load pertama kali di MyApplication sebelum appModule.
 */
fun androidPlatformModule(): Module = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { SettingsFactory(androidContext()) }
}
