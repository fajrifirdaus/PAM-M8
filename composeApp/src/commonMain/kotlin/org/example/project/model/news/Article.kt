package org.example.project.model.news

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════
// Article.kt — commonMain
// Model data untuk respons dari NewsAPI.org
//
// Endpoint: GET https://newsapi.org/v2/top-headlines?country=id&apiKey=...
// Response structure:
//   { "status": "ok", "totalResults": 100, "articles": [ {...}, ... ] }
// ═══════════════════════════════════════════════════════════════════════

// ── Wrapper response dari NewsAPI ─────────────────────────────────────
@Serializable
data class NewsApiResponse(
    val status       : String        = "",
    val totalResults : Int           = 0,
    val articles     : List<Article> = emptyList(),
    val code         : String?       = null,   // error code jika status != "ok"
    val message      : String?       = null    // error message
)

// ── Model sumber berita ───────────────────────────────────────────────
@Serializable
data class NewsSource(
    val id   : String? = null,
    val name : String  = "Unknown"
)

// ── Model artikel utama ───────────────────────────────────────────────
@Serializable
data class Article(
    // ID internal (dibuat dari hashCode karena NewsAPI tidak punya integer id)
    val id             : Int           = 0,

    // Field dari NewsAPI
    val source         : NewsSource    = NewsSource(),
    val author         : String?       = null,
    val title          : String        = "",
    val description    : String?       = null,
    val url            : String        = "",

    @SerialName("urlToImage")
    val urlToImage     : String?       = null,    // ← URL gambar asli dari NewsAPI

    @SerialName("publishedAt")
    val publishedAt    : String        = "",

    val content        : String?       = null
) {
    // ── Computed helpers ──────────────────────────────────────────────

    /** Nama sumber berita, fallback ke author jika source kosong */
    val sourceName: String
        get() = source.name.takeIf { it.isNotBlank() && it != "Unknown" }
            ?: author?.split(",")?.firstOrNull()?.trim()
            ?: "Unknown"

    /** Deskripsi atau konten singkat untuk preview card */
    val preview: String
        get() = description?.takeIf { it.isNotBlank() }
            ?: content?.substringBefore("[")?.trim()?.takeIf { it.isNotBlank() }
            ?: "Baca selengkapnya..."

    /** Konten lengkap untuk detail screen */
    val fullContent: String
        get() = content
            ?.substringBefore("[+")   // hapus "[+1234 chars]" suffix dari NewsAPI
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: description?.takeIf { it.isNotBlank() }
            ?: "Konten tidak tersedia. Buka URL untuk membaca artikel lengkap."

    /** Kategori berdasarkan keyword di judul/deskripsi */
    val category: String
        get() {
            val text = (title + " " + (description ?: "")).lowercase()
            return when {
                text.containsAny("teknologi", "digital", "ai", "robot", "software", "aplikasi", "internet", "cyber", "gadget") -> "Teknologi"
                text.containsAny("bisnis", "ekonomi", "saham", "investasi", "keuangan", "rupiah", "bank", "pasar") -> "Bisnis"
                text.containsAny("olahraga", "sepak bola", "basket", "bulu tangkis", "liga", "gol", "turnamen") -> "Olahraga"
                text.containsAny("kesehatan", "medis", "vaksin", "dokter", "penyakit", "rumah sakit", "virus") -> "Kesehatan"
                text.containsAny("pendidikan", "sekolah", "universitas", "mahasiswa", "siswa", "belajar") -> "Pendidikan"
                else -> "Umum"
            }
        }

    /** Estimasi waktu baca berdasarkan panjang konten */
    val readTime: String
        get() {
            val words = (description ?: "").split(" ").size +
                        (content ?: "").split(" ").size
            return "${(words / 200).coerceAtLeast(1)} menit"
        }

    /** Format tanggal publish yang lebih mudah dibaca */
    val publishedDisplay: String
        get() = if (publishedAt.length >= 10) {
            val parts = publishedAt.substring(0, 10).split("-")
            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else publishedAt
        } else publishedAt
}

private fun String.containsAny(vararg keywords: String): Boolean =
    keywords.any { this.contains(it) }
