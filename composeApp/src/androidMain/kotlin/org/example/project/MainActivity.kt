package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// ═══════════════════════════════════════════════════════════════════════
// MainActivity.kt — androidMain
// Pertemuan 8: MainActivity tidak lagi membuat dependencies manual.
// Semua di-inject oleh Koin yang sudah diinisialisasi di MyApplication.
// ═══════════════════════════════════════════════════════════════════════

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()   // Tidak ada parameter — Koin yang menyediakan semua deps
        }
    }
}
