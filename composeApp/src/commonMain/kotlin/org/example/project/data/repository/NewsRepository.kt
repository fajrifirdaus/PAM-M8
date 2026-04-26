package org.example.project.data.repository

import org.example.project.data.api.NewsApi
import org.example.project.data.cache.ArticleCache
import org.example.project.model.news.Article

// ═══════════════════════════════════════════════════════════════════════
// NewsRepository.kt — commonMain
// Repository pattern untuk NewsAPI.org
//
// Cache strategy (TTL 5 menit):
//   1. Cek cache → valid? → return cache (hemat request)
//   2. Fetch dari NewsAPI → sukses? → simpan cache, return
//   3. Fetch gagal + cache ada → return cache lama (offline mode)
//   4. Fetch gagal + no cache → return Error
// ═══════════════════════════════════════════════════════════════════════

class NewsRepository(
    private val api   : NewsApi,
    private val cache : ArticleCache = ArticleCache()
) {

    // ── GET semua artikel (top headlines) ─────────────────────────────

    suspend fun getArticles(forceRefresh: Boolean = false): Result<List<Article>> {
        if (!forceRefresh && cache.isListCacheValid()) {
            return Result.success(cache.getArticles())
        }
        return try {
            val articles = api.getTopHeadlines()
            cache.saveArticles(articles)
            Result.success(articles)
        } catch (e: Exception) {
            if (cache.hasArticles()) Result.success(cache.getArticles())
            else Result.failure(e)
        }
    }

    // ── GET artikel berdasarkan kategori ──────────────────────────────

    suspend fun getArticlesByCategory(
        category     : String,
        forceRefresh : Boolean = false
    ): Result<List<Article>> {
        if (category == "Semua") return getArticles(forceRefresh)
        return try {
            val articles = api.getTopHeadlines(category = category)
            Result.success(articles)
        } catch (e: Exception) {
            // Fallback: filter dari cache list
            val cached = cache.getArticles().filter { it.category == category }
            if (cached.isNotEmpty()) Result.success(cached)
            else Result.failure(e)
        }
    }

    // ── GET detail artikel by ID ──────────────────────────────────────
    // NewsAPI tidak punya endpoint /articles/{id}, jadi ambil dari cache

    suspend fun getArticleById(id: Int, forceRefresh: Boolean = false): Result<Article> {
        if (!forceRefresh) {
            cache.getArticle(id)?.let { return Result.success(it) }
            cache.getArticles().find { it.id == id }?.let {
                cache.saveArticle(it)
                return Result.success(it)
            }
        }
        // Jika tidak ada di cache, refresh semua dulu
        return try {
            val articles = api.getTopHeadlines()
            cache.saveArticles(articles)
            articles.find { it.id == id }
                ?.let { Result.success(it) }
                ?: Result.failure(Exception("Artikel tidak ditemukan"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    fun getCacheInfo() = cache.getCacheInfo()
    fun clearCache()   = cache.clearAll()
}
