package org.example.project.data.cache

import org.example.project.model.news.Article

// ═══════════════════════════════════════════════════
// ARTICLE CACHE — Offline Caching (BONUS +10%)
// ─────────────────────────────────────────────────
// Menyimpan data artikel di memory untuk akses offline.
// Saat jaringan tidak tersedia, data dari cache tetap
// bisa ditampilkan kepada pengguna.
//
// Cache strategy:
//   - TTL (Time-To-Live): 5 menit
//   - Jika cache expired → fetch ulang dari API
//   - Jika fetch gagal tapi cache ada → pakai cache
//   - Jika fetch gagal dan cache kosong → tampilkan Error
// ═══════════════════════════════════════════════════

class ArticleCache {

    // Durasi cache valid: 5 menit (dalam milidetik)
    private val cacheTtlMs = 5 * 60 * 1000L

    // In-memory store
    private var cachedArticles: List<Article>   = emptyList()
    private var cachedDetail: Map<Int, Article> = emptyMap()
    private var lastFetchTime: Long = 0L
    private var detailFetchTime: MutableMap<Int, Long> = mutableMapOf()

    // ── Articles list cache ──

    fun isListCacheValid(): Boolean =
        cachedArticles.isNotEmpty() &&
        (System.currentTimeMillis() - lastFetchTime) < cacheTtlMs

    fun getArticles(): List<Article> = cachedArticles

    fun saveArticles(articles: List<Article>) {
        cachedArticles = articles
        lastFetchTime  = System.currentTimeMillis()
    }

    fun hasArticles(): Boolean = cachedArticles.isNotEmpty()

    // ── Detail cache ──

    fun isDetailCacheValid(id: Int): Boolean {
        val fetchTime = detailFetchTime[id] ?: return false
        return (System.currentTimeMillis() - fetchTime) < cacheTtlMs
    }

    fun getArticle(id: Int): Article? = cachedDetail[id]

    fun saveArticle(article: Article) {
        cachedDetail = cachedDetail + (article.id to article)
        detailFetchTime[article.id] = System.currentTimeMillis()
    }

    // ── Cache info ──

    fun getCacheInfo(): CacheInfo = CacheInfo(
        articleCount  = cachedArticles.size,
        lastFetchTime = lastFetchTime,
        isValid       = isListCacheValid()
    )

    fun clearAll() {
        cachedArticles  = emptyList()
        cachedDetail    = emptyMap()
        lastFetchTime   = 0L
        detailFetchTime = mutableMapOf()
    }
}

data class CacheInfo(
    val articleCount: Int,
    val lastFetchTime: Long,
    val isValid: Boolean
) {
    val lastFetchDisplay: String
        get() = if (lastFetchTime == 0L) "Belum pernah"
        else {
            val elapsed = System.currentTimeMillis() - lastFetchTime
            when {
                elapsed < 60_000      -> "Baru saja"
                elapsed < 3_600_000   -> "${elapsed / 60_000} menit lalu"
                else                  -> "${elapsed / 3_600_000} jam lalu"
            }
        }
}
