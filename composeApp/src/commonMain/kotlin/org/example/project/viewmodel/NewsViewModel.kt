package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.data.api.HttpClientFactory
import org.example.project.data.api.NewsApi
import org.example.project.data.cache.ArticleCache
import org.example.project.data.repository.NewsRepository
import org.example.project.model.news.Article
import org.example.project.platform.NetworkMonitor
import org.example.project.ui.states.UiState

// ═══════════════════════════════════════════════════════════════════════
// NewsViewModel.kt — commonMain
// Pertemuan 8 + NewsAPI:
//   - NetworkMonitor: status koneksi real-time + auto-retry
//   - selectedCategory: filter via NewsAPI category endpoint
// ═══════════════════════════════════════════════════════════════════════

data class NewsScreenState(
    val articlesState    : UiState<List<Article>> = UiState.Loading,
    val detailState      : UiState<Article>       = UiState.Loading,
    val isRefreshing     : Boolean                = false,
    val searchQuery      : String                 = "",
    val selectedCategory : String                 = "Semua",
    val cacheInfo        : org.example.project.data.cache.CacheInfo? = null,
    val snackbarMessage  : String?                = null,
    val isFromCache      : Boolean                = false,
    val isConnected      : Boolean                = true
)

class NewsViewModel(
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val client     = HttpClientFactory.create()
    private val api        = NewsApi(client)
    private val cache      = ArticleCache()
    private val repository = NewsRepository(api, cache)

    private val _state = MutableStateFlow(NewsScreenState())
    val state: StateFlow<NewsScreenState> = _state.asStateFlow()

    val categories = listOf(
        "Semua", "Teknologi", "Bisnis", "Olahraga",
        "Kesehatan", "Sains", "Hiburan"
    )

    init {
        observeNetworkStatus()
        loadArticles()
    }

    // ── Network status real-time ───────────────────────────────────────

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.observeConnectivity()
                .distinctUntilChanged()
                .collect { connected ->
                    val wasOffline = !_state.value.isConnected
                    _state.update { it.copy(isConnected = connected) }
                    // Auto-retry saat koneksi pulih dari offline + artikel gagal
                    if (connected && wasOffline &&
                        _state.value.articlesState is UiState.Error) {
                        loadArticles()
                    }
                }
        }
    }

    // ── Load / Refresh ─────────────────────────────────────────────────

    fun loadArticles(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(articlesState = UiState.Loading, isRefreshing = forceRefresh) }
            val category = _state.value.selectedCategory

            val result = if (category == "Semua") {
                repository.getArticles(forceRefresh)
            } else {
                repository.getArticlesByCategory(category, forceRefresh)
            }

            result
                .onSuccess { articles ->
                    _state.update {
                        it.copy(
                            articlesState   = UiState.Success(articles),
                            isRefreshing    = false,
                            cacheInfo       = repository.getCacheInfo(),
                            isFromCache     = !forceRefresh && repository.getCacheInfo().isValid,
                            snackbarMessage = if (forceRefresh) "Berita diperbarui!" else null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            articlesState = UiState.Error(
                                error.message ?: "Gagal memuat berita."
                            ),
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    fun refresh() = loadArticles(forceRefresh = true)

    fun loadArticleDetail(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(detailState = UiState.Loading) }
            repository.getArticleById(id)
                .onSuccess { article ->
                    _state.update { it.copy(detailState = UiState.Success(article)) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(detailState = UiState.Error(
                            error.message ?: "Artikel tidak ditemukan."
                        ))
                    }
                }
        }
    }

    // ── Filter & Search ───────────────────────────────────────────────

    fun updateSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: String) {
        if (_state.value.selectedCategory == category) return
        _state.update { it.copy(selectedCategory = category) }
        loadArticles(forceRefresh = false)
    }

    /** Filter client-side berdasarkan searchQuery */
    fun getFilteredArticles(articles: List<Article>): List<Article> {
        val q = _state.value.searchQuery.trim().lowercase()
        return if (q.isEmpty()) articles
        else articles.filter { a ->
            a.title.lowercase().contains(q) ||
            (a.description?.lowercase()?.contains(q) == true) ||
            a.sourceName.lowercase().contains(q)
        }
    }

    fun dismissSnackbar() {
        _state.update { it.copy(snackbarMessage = null) }
    }
}
