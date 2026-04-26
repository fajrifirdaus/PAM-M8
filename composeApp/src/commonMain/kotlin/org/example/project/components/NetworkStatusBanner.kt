package org.example.project.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════════
// NetworkStatusBanner.kt — commonMain
// Composable banner yang tampil otomatis saat offline / kembali online
// Pertemuan 8: Platform-Specific Features — NetworkMonitor
// ═══════════════════════════════════════════════════════════════════════

/**
 * Banner status koneksi internet.
 *
 * - Offline  → banner MERAH muncul dengan slide-in dari atas
 * - Online   → banner MERAH hilang; banner HIJAU muncul selama 2 detik
 *
 * @param isConnected  true jika ada koneksi aktif dan tervalidasi
 * @param modifier     modifier opsional untuk container
 */
@Composable
fun NetworkStatusBanner(
    isConnected: Boolean,
    modifier   : Modifier = Modifier
) {
    var showOnlineBanner by remember { mutableStateOf(false) }
    var wasOffline       by remember { mutableStateOf(false) }

    // Deteksi transisi offline → online untuk tampilkan banner hijau
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            wasOffline = true
        } else if (wasOffline) {
            showOnlineBanner = true
            delay(2000)
            showOnlineBanner = false
            wasOffline = false
        }
    }

    Column(modifier = modifier) {

        // ── Banner Offline (merah) ─────────────────────────────────────
        AnimatedVisibility(
            visible = !isConnected,
            enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit    = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color    = MaterialTheme.colorScheme.error
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.CloudOff,
                        contentDescription = "Tidak ada koneksi",
                        tint               = MaterialTheme.colorScheme.onError,
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "Tidak ada koneksi internet",
                        color      = MaterialTheme.colorScheme.onError,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── Banner Kembali Online (hijau/tertiary) ─────────────────────
        AnimatedVisibility(
            visible = showOnlineBanner,
            enter   = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit    = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color    = MaterialTheme.colorScheme.tertiary
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.CloudQueue,
                        contentDescription = "Kembali online",
                        tint               = MaterialTheme.colorScheme.onTertiary,
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "Koneksi pulih",
                        color      = MaterialTheme.colorScheme.onTertiary,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
