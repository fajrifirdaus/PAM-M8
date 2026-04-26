package org.example.project.di

import org.example.project.data.api.HttpClientFactory
import org.example.project.data.api.NewsApi
import org.example.project.data.cache.ArticleCache
import org.example.project.data.repository.NewsRepository
import org.example.project.data.repository.NoteRepository
import org.example.project.data.repository.SettingsManager
import org.example.project.db.AppDatabase
import org.example.project.platform.BatteryInfo
import org.example.project.platform.DeviceInfo
import org.example.project.platform.NetworkMonitor
import org.example.project.viewmodel.NewsViewModel
import org.example.project.viewmodel.NotesViewModel
import org.example.project.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// ═══════════════════════════════════════════════════════════════════════
// AppModule.kt — commonMain
// Definisi semua Koin dependencies untuk seluruh aplikasi
//
// URUTAN LOAD di MyApplication:
//   1. androidPlatformModule()  ← DatabaseDriverFactory(ctx), SettingsFactory(ctx)
//   2. appModule                ← semua yang lain
//
// Pertemuan 8: Dependency Injection dengan Koin
// ═══════════════════════════════════════════════════════════════════════

val appModule = module {

    // ── Database ──────────────────────────────────────────────────────
    // DatabaseDriverFactory di-provide oleh androidPlatformModule()
    single<AppDatabase> {
        AppDatabase(get<DatabaseDriverFactory>().createDriver())
    }

    // ── Settings ──────────────────────────────────────────────────────
    // SettingsFactory di-provide oleh androidPlatformModule()
    single<SettingsManager> {
        SettingsManager(get<SettingsFactory>().createSettings())
    }

    // ── Networking ────────────────────────────────────────────────────
    single { HttpClientFactory.create() }
    single { NewsApi(get()) }

    // ── Cache (in-memory, no constructor arg) ─────────────────────────
    single { ArticleCache() }

    // ── Repositories ──────────────────────────────────────────────────
    single { NoteRepository(get()) }
    single { NewsRepository(api = get(), cache = get()) }

    // ── Platform APIs (expect/actual) ─────────────────────────────────
    single { DeviceInfo() }
    single { NetworkMonitor() }
    single { BatteryInfo() }    // BONUS

    // ── ViewModels ────────────────────────────────────────────────────
    // Koin otomatis resolve semua constructor params dari graph di atas
    viewModelOf(::NotesViewModel)
    // NewsViewModel & ProfileViewModel: no-arg constructor, self-create deps
    // Tetap didaftarkan agar koinViewModel() di Composable bekerja
    viewModelOf(::NewsViewModel)
    viewModelOf(::ProfileViewModel)
}
