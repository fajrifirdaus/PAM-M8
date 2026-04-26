package org.example.project.data.api

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// ═══════════════════════════════════════════════════
// HTTP CLIENT FACTORY
// Membuat instance HttpClient dengan konfigurasi:
//   - ContentNegotiation (JSON parsing)
//   - Logging (debug)
//   - ignoreUnknownKeys (toleran terhadap field baru di API)
// ═══════════════════════════════════════════════════

object HttpClientFactory {
    fun create(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint        = true
                isLenient          = true
                ignoreUnknownKeys  = true   // Abaikan field tidak dikenal
                encodeDefaults     = true
            })
        }
        install(Logging) {
            level  = LogLevel.HEADERS       // Hanya log header di produksi
            logger = Logger.DEFAULT
        }
    }
}
