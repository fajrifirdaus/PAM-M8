package org.example.project.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

// ═══════════════════════════════════════════════════════════════════════
// KoinHelper.kt — commonMain
// Helper function untuk inisialisasi Koin di platform non-Android
// (iOS, Desktop). Di Android, inisialisasi dilakukan di MyApplication.
// Pertemuan 8: Dependency Injection dengan Koin
// ═══════════════════════════════════════════════════════════════════════

fun initKoin(additionalConfig: KoinApplication.() -> Unit = {}): KoinApplication =
    startKoin {
        modules(appModule)
        additionalConfig()
    }
