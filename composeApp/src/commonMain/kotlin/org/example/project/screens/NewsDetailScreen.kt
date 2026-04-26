package org.example.project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.example.project.components.NewsImage
import org.example.project.ui.states.UiState
import org.example.project.viewmodel.NewsViewModel

// ═══════════════════════════════════════════════════════════════════════
// NewsDetailScreen.kt — commonMain
// Detail artikel dengan hero image asli dari NewsAPI (urlToImage)
// ═══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    articleId : Int,
    viewModel : NewsViewModel,
    isDarkMode: Boolean,
    onBack    : () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(articleId) {
        viewModel.loadArticleDetail(articleId)
    }

    val chipBg   = if (isDarkMode) Color(0xFF60A5FA) else Color(0x220061FF)
    val chipText = if (isDarkMode) Color(0xFF0D1B2A) else Color(0xFF003D99)

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text("Detail Berita", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, "Kembali")
                    }
                },
                windowInsets = WindowInsets(0.dp),
                colors       = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val detail = state.detailState) {

                // ── Loading ───────────────────────────────────────────
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(16.dp))
                            Text("Memuat artikel...",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                    }
                }

                // ── Success ───────────────────────────────────────────
                is UiState.Success -> {
                    val article = detail.data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // ── Hero Image (gambar asli dari NewsAPI) ─────
                        NewsImage(
                            imageUrl   = article.urlToImage,
                            category   = article.category,
                            articleId  = article.id,
                            isDarkMode = isDarkMode,
                            modifier   = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )

                        Column(modifier = Modifier.padding(20.dp)) {

                            // ── Kategori + waktu baca ─────────────────
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Surface(shape = RoundedCornerShape(50.dp), color = chipBg) {
                                    Text(
                                        article.category,
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color      = chipText,
                                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Schedule, null,
                                        modifier = Modifier.size(13.dp),
                                        tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    Spacer(Modifier.width(4.dp))
                                    Text(article.readTime,
                                        fontSize = 12.sp,
                                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // ── Judul ─────────────────────────────────
                            Text(
                                text       = article.title,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 30.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            // ── Meta: sumber + tanggal ────────────────
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Article, null,
                                        modifier = Modifier.size(14.dp),
                                        tint     = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        article.sourceName,
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (article.publishedDisplay.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CalendarToday, null,
                                            modifier = Modifier.size(12.dp),
                                            tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            article.publishedDisplay,
                                            fontSize = 12.sp,
                                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }

                            // ── Author (jika ada) ─────────────────────
                            if (!article.author.isNullOrBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Person, null,
                                        modifier = Modifier.size(14.dp),
                                        tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        article.author.split(",").firstOrNull()?.trim() ?: article.author,
                                        fontSize = 12.sp,
                                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )

                            // ── Konten artikel ────────────────────────
                            Text(
                                text       = article.fullContent,
                                fontSize   = 16.sp,
                                lineHeight = 26.sp,
                                color      = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                            )

                            // ── Peringatan konten terbatas ────────────
                            // NewsAPI free tier hanya kirim 200 char pertama
                            if (article.fullContent.length < 400) {
                                Spacer(Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier          = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.Info, null,
                                            modifier = Modifier.size(16.dp),
                                            tint     = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "Konten dibatasi oleh NewsAPI free tier. Buka URL asli untuk artikel lengkap.",
                                            fontSize   = 12.sp,
                                            lineHeight = 18.sp,
                                            color      = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }

                // ── Error ─────────────────────────────────────────────
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(32.dp)
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, null,
                                modifier = Modifier.size(64.dp),
                                tint     = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                            Spacer(Modifier.height(16.dp))
                            Text(detail.message,
                                color     = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center)
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = { viewModel.loadArticleDetail(articleId) },
                                shape   = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Coba Lagi", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
