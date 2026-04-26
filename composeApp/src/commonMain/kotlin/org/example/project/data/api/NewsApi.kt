package org.example.project.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.project.model.news.Article
import org.example.project.model.news.NewsApiResponse

// ═══════════════════════════════════════════════════════════════════════
// NewsApi.kt — commonMain
// HTTP client ke NewsAPI.org
//
// Base URL: https://newsapi.org/v2
// Endpoints:
//   GET /top-headlines  → Berita utama (by country atau kategori)
//   GET /everything     → Pencarian berita bebas
//
// ⚠️  PENTING: Ganti YOUR_NEWSAPI_KEY dengan API key kamu dari newsapi.org
//     Free tier: 100 request/hari, hanya untuk development (localhost)
// ═══════════════════════════════════════════════════════════════════════

class NewsApi(private val client: HttpClient) {

    private val baseUrl = "https://newsapi.org/v2"

    // ⚠️  GANTI dengan API key milikmu dari https://newsapi.org/register
    private val apiKey  = "738173e26329496c90197ec5d6526145"

    /**
     * Ambil berita utama Indonesia.
     * Endpoint: GET /top-headlines?country=id&pageSize=40
     */
    suspend fun getTopHeadlines(
        country  : String = "us",
        category : String? = null,
        pageSize : Int    = 40,
        page     : Int    = 1
    ): List<Article> {
        val response: NewsApiResponse = client.get("$baseUrl/top-headlines") {
            parameter("country",  country)
            parameter("pageSize", pageSize)
            parameter("page",     page)
            parameter("apiKey",   apiKey)
            if (category != null && category != "Semua") {
                parameter("category", mapCategory(category))
            }
        }.body()

        if (response.status != "ok") {
            throw Exception(response.message ?: "NewsAPI error: ${response.code}")
        }

        // Tambahkan id unik berdasarkan index karena NewsAPI tidak punya integer id
        return response.articles
            .filter { it.title.isNotBlank() && it.title != "[Removed]" }
            .mapIndexed { index, article -> article.copy(id = index + 1) }
    }

    /**
     * Cari berita berdasarkan query teks.
     * Endpoint: GET /everything?q=...&language=id
     */
    suspend fun searchArticles(
        query    : String,
        pageSize : Int = 20
    ): List<Article> {
        val response: NewsApiResponse = client.get("$baseUrl/everything") {
            parameter("q",        query)
            parameter("language", "id")
            parameter("pageSize", pageSize)
            parameter("sortBy",   "publishedAt")
            parameter("apiKey",   apiKey)
        }.body()

        if (response.status != "ok") {
            throw Exception(response.message ?: "NewsAPI error: ${response.code}")
        }

        return response.articles
            .filter { it.title.isNotBlank() && it.title != "[Removed]" }
            .mapIndexed { index, article -> article.copy(id = index + 1) }
    }

    /**
     * Map nama kategori Indonesia ke kategori NewsAPI.
     */
    private fun mapCategory(category: String): String = when (category) {
        "Teknologi"  -> "technology"
        "Bisnis"     -> "business"
        "Olahraga"   -> "sports"
        "Kesehatan"  -> "health"
        "Sains"      -> "science"
        "Hiburan"    -> "entertainment"
        else         -> "general"
    }
}
