package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.components.ArticleCard
import org.example.project.components.ArticleCardSkeleton
import org.example.project.components.NetworkStatusBanner
import org.example.project.ui.states.UiState
import org.example.project.viewmodel.NewsViewModel

// ═══════════════════════════════════════════════════════════════════════
// NewsListScreen.kt — commonMain
// Pertemuan 8: Ditambahkan NetworkStatusBanner di atas search bar
//   - Banner merah muncul saat offline
//   - Banner hijau 2 detik saat koneksi pulih
//   - Auto-retry load artikel saat kembali online (logic di NewsViewModel)
// ═══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    viewModel     : NewsViewModel,
    isDarkMode    : Boolean,
    onArticleClick: (Int) -> Unit
) {
    val state     by viewModel.state.collectAsState()
    val pullState  = rememberPullToRefreshState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Network Status Banner (Pertemuan 8) ───────────────────
            // Sama seperti NotesScreen — muncul otomatis saat offline
            NetworkStatusBanner(
                isConnected = state.isConnected,
                modifier    = Modifier.fillMaxWidth()
            )

            // ── Search bar ────────────────────────────────────────────
            OutlinedTextField(
                value         = state.searchQuery,
                onValueChange = { viewModel.updateSearch(it) },
                placeholder   = { Text("Cari berita...") },
                leadingIcon   = {
                    Icon(Icons.Rounded.Search, null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon  = {
                    AnimatedVisibility(
                        visible = state.searchQuery.isNotEmpty(),
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        IconButton(onClick = { viewModel.updateSearch("") }) {
                            Icon(Icons.Rounded.Close, null)
                        }
                    }
                },
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape      = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // ── Filter kategori chips ─────────────────────────────────
            LazyRow(
                contentPadding      = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier            = Modifier.padding(bottom = 8.dp)
            ) {
                items(viewModel.categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat,
                        onClick  = { viewModel.selectCategory(cat) },
                        label    = { Text(cat, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = if (state.selectedCategory == cat) {
                            { Icon(Icons.Rounded.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // ── Cache indicator ───────────────────────────────────────
            state.cacheInfo?.let { info ->
                if (info.articleCount > 0) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Cached, null,
                            modifier = Modifier.size(12.dp),
                            tint     = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Cache: ${info.articleCount} artikel · ${info.lastFetchDisplay}",
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // ── Content dengan PullToRefreshBox ───────────────────────
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh    = { viewModel.refresh() },
                state        = pullState,
                modifier     = Modifier.fillMaxSize()
            ) {
                when (val s = state.articlesState) {

                    // LOADING STATE — skeleton cards
                    is UiState.Loading -> {
                        LazyColumn(
                            contentPadding      = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(4) { ArticleCardSkeleton() }
                        }
                    }

                    // SUCCESS STATE — daftar artikel
                    is UiState.Success -> {
                        val filtered = viewModel.getFilteredArticles(s.data)

                        if (filtered.isEmpty()) {
                            Box(
                                modifier        = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Rounded.SearchOff, null,
                                        modifier = Modifier.size(64.dp),
                                        tint     = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "Tidak ada hasil",
                                        fontWeight = FontWeight.Bold,
                                        color      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        "Coba kata kunci atau kategori lain",
                                        fontSize = 13.sp,
                                        color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding      = PaddingValues(
                                    start  = 16.dp, end    = 16.dp,
                                    top    = 8.dp,  bottom = 80.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Row(
                                        modifier              = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment     = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${filtered.size} Artikel",
                                            fontSize   = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                        )
                                        if (state.isFromCache) {
                                            Surface(
                                                shape = RoundedCornerShape(50.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    "Dari cache",
                                                    fontSize   = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color      = MaterialTheme.colorScheme.primary,
                                                    modifier   = Modifier.padding(
                                                        horizontal = 8.dp, vertical = 3.dp
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                items(filtered, key = { it.id }) { article ->
                                    ArticleCard(
                                        article    = article,
                                        isDarkMode = isDarkMode,
                                        onClick    = { onArticleClick(article.id) }
                                    )
                                }
                            }
                        }
                    }

                    // ERROR STATE — pesan error + info koneksi + retry
                    is UiState.Error -> {
                        Box(
                            modifier        = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier            = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    if (!state.isConnected) Icons.Rounded.WifiOff
                                    else Icons.Rounded.ErrorOutline,
                                    null,
                                    modifier = Modifier.size(72.dp),
                                    tint     = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    if (!state.isConnected) "Tidak Ada Koneksi"
                                    else "Gagal Memuat Berita",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 18.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (!state.isConnected)
                                        "Periksa koneksi internet. Artikel akan dimuat otomatis saat koneksi pulih."
                                    else s.message,
                                    fontSize  = 13.sp,
                                    color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(24.dp))
                                Button(
                                    onClick = { viewModel.refresh() },
                                    shape   = RoundedCornerShape(12.dp),
                                    enabled = state.isConnected
                                ) {
                                    Icon(Icons.Rounded.Refresh, null,
                                        modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (state.isConnected) "Coba Lagi"
                                        else "Menunggu Koneksi...",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Snackbar sukses ───────────────────────────────────────────
        state.snackbarMessage?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(2500)
                viewModel.dismissSnackbar()
            }
            Surface(
                modifier        = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                color           = MaterialTheme.colorScheme.primary,
                shape           = RoundedCornerShape(50.dp),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.CheckCircle, null,
                        tint     = Color.White,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(msg, color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
