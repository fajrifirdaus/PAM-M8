package org.example.project.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.example.project.model.news.Article
import org.example.project.ui.theme.AzureBlue
import org.example.project.ui.theme.AzureLight

// ═══════════════════════════════════════════════════════════════════════
// ArticleCard.kt — commonMain
// Card berita dengan gambar asli dari NewsAPI (urlToImage)
// Menggunakan Coil3 AsyncImage untuk load gambar async
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun ArticleCard(
    article   : Article,
    isDarkMode: Boolean,
    onClick   : () -> Unit,
    modifier  : Modifier = Modifier
) {
    val accentColor = if (isDarkMode) AzureLight else AzureBlue
    val chipBg      = if (isDarkMode) Color(0xFF60A5FA) else Color(0x220061FF)
    val chipText    = if (isDarkMode) Color(0xFF0D1B2A) else Color(0xFF003D99)

    Card(
        modifier  = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // ── Gambar Artikel ────────────────────────────────────────
            NewsImage(
                imageUrl     = article.urlToImage,
                category     = article.category,
                articleId    = article.id,
                isDarkMode   = isDarkMode,
                modifier     = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // ── Header: kategori + waktu baca ─────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(50.dp), color = chipBg) {
                        Text(
                            text       = article.category,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = chipText,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Schedule, null,
                            modifier = Modifier.size(12.dp),
                            tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text  = article.readTime,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── Judul ─────────────────────────────────────────────
                Text(
                    text       = article.title,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(6.dp))

                // ── Preview ───────────────────────────────────────────
                Text(
                    text      = article.preview,
                    fontSize  = 13.sp,
                    color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis,
                    lineHeight = 19.sp
                )

                Spacer(Modifier.height(12.dp))

                // ── Footer: sumber + tanggal ──────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape    = RoundedCornerShape(50.dp),
                            color    = accentColor.copy(alpha = 0.1f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Article, null,
                                    tint     = accentColor,
                                    modifier = Modifier.size(13.dp))
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = article.sourceName,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = accentColor,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                    }
                    if (article.publishedDisplay.isNotBlank()) {
                        Text(
                            text     = article.publishedDisplay,
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }
    }
}

// ── NewsImage: AsyncImage dengan fallback placeholder ──────────────────

@Composable
fun NewsImage(
    imageUrl  : String?,
    category  : String,
    articleId : Int,
    isDarkMode: Boolean,
    modifier  : Modifier = Modifier
) {
    val placeholderColor = articlePlaceholderColor(articleId, isDarkMode)

    Box(modifier = modifier) {
        // Placeholder background selalu tampil sebagai fallback
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .background(placeholderColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = articleIcon(category),
                contentDescription = null,
                tint               = Color.White.copy(alpha = 0.35f),
                modifier           = Modifier.size(56.dp)
            )
        }

        // AsyncImage load gambar asli dari NewsAPI
        if (!imageUrl.isNullOrBlank()) {
            val context = LocalPlatformContext.current
            var isError by remember { mutableStateOf(false) }

            if (!isError) {
                AsyncImage(
                    model             = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Gambar artikel",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize(),
                    onError            = { isError = true }
                )
            }
        }

        // Gradient overlay di bawah gambar agar teks lebih terbaca
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f)
                        )
                    )
                )
        )
    }
}

// ── Helpers ────────────────────────────────────────────────────────────

private fun articlePlaceholderColor(id: Int, isDark: Boolean): Color {
    val dark  = listOf(
        Color(0xFF1A3A5C), Color(0xFF1A3D2E), Color(0xFF3D1A1A),
        Color(0xFF2E1A3D), Color(0xFF3D2E1A), Color(0xFF1A2E3D),
        Color(0xFF2A1A3D)
    )
    val light = listOf(
        Color(0xFF4A90D9), Color(0xFF27AE60), Color(0xFFE74C3C),
        Color(0xFF8E44AD), Color(0xFFE67E22), Color(0xFF16A085),
        Color(0xFF2980B9)
    )
    return (if (isDark) dark else light)[id % dark.size]
}

@Composable
private fun articleIcon(category: String) = when (category) {
    "Teknologi"  -> Icons.Rounded.Devices
    "Bisnis"     -> Icons.Rounded.ShowChart
    "Olahraga"   -> Icons.Rounded.EmojiEvents
    "Kesehatan"  -> Icons.Rounded.Favorite
    "Pendidikan" -> Icons.Rounded.School
    "Sains"      -> Icons.Rounded.Science
    "Hiburan"    -> Icons.Rounded.TheaterComedy
    else         -> Icons.Rounded.Article
}

// ── Skeleton loading card ───────────────────────────────────────────────

@Composable
fun ArticleCardSkeleton() {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (i == 2) 0.5f else 1f)
                            .height(if (i == 0) 16.dp else 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    if (i < 2) Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
