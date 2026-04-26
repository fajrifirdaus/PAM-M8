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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.viewmodel.NotesViewModel

// ═══════════════════════════════════════════════════════════════════════
// SettingsScreen.kt — commonMain
// Pertemuan 8:
//   - Device Info: model, produsen, OS, jenis perangkat
//   - Battery: realtime via polling 30 detik + tombol refresh manual
//   - Countdown menunjukkan sisa waktu polling berikutnya
// ═══════════════════════════════════════════════════════════════════════

private const val BATTERY_POLL_INTERVAL_S = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel       : NotesViewModel,
    isDarkMode      : Boolean,
    onToggleDarkMode: () -> Unit,
    onBack          : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Countdown detik hingga polling otomatis berikutnya
    var countdown by remember { mutableStateOf(BATTERY_POLL_INTERVAL_S) }

    // Saat screen aktif: refresh segera, lalu countdown ulang setiap detik
    LaunchedEffect(Unit) {
        viewModel.refreshBatteryInfo()
        countdown = BATTERY_POLL_INTERVAL_S
        while (true) {
            delay(1000)
            countdown = (countdown - 1).coerceAtLeast(0)
            // Reset saat mencapai 0 — polling otomatis sudah berjalan di ViewModel
            if (countdown == 0) {
                countdown = BATTERY_POLL_INTERVAL_S
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Kembali")
                    }
                },
                windowInsets = WindowInsets(0.dp),
                colors       = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ── Tampilan ───────────────────────────────────────────────
            SettingsSectionTitle("Tampilan")

            SettingsItem(
                title    = "Mode Gelap",
                subtitle = if (isDarkMode) "Aktif" else "Nonaktif",
                icon     = if (isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                trailing = {
                    Switch(
                        checked         = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() }
                    )
                }
            )

            Spacer(Modifier.height(8.dp))

            // ── Informasi Perangkat ────────────────────────────────────
            SettingsSectionTitle("Informasi Perangkat")

            SettingsItem(
                title    = "Model Perangkat",
                subtitle = uiState.deviceName.ifEmpty { "Memuat..." },
                icon     = Icons.Rounded.Smartphone
            )
            SettingsItem(
                title    = "Produsen",
                subtitle = uiState.manufacturer.ifEmpty { "Memuat..." },
                icon     = Icons.Rounded.Business
            )
            SettingsItem(
                title    = "Versi OS",
                subtitle = uiState.osVersion.ifEmpty { "Memuat..." },
                icon     = Icons.Rounded.Android
            )
            SettingsItem(
                title    = "Jenis Perangkat",
                subtitle = when {
                    uiState.deviceName.isEmpty() -> "Memuat..."
                    uiState.isTablet             -> "Tablet (layar ≥ 7 inci)"
                    else                         -> "Smartphone"
                },
                icon = if (uiState.isTablet) Icons.Rounded.TabletAndroid
                       else Icons.Rounded.PhoneAndroid
            )

            Spacer(Modifier.height(8.dp))

            // ── Status Baterai (realtime) ──────────────────────────────
            // Header dengan tombol refresh manual + countdown polling
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "STATUS BATERAI",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.primary,
                    modifier   = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Countdown polling otomatis
                    Text(
                        text     = "auto ${countdown}s",
                        fontSize = 10.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(6.dp))
                    // Tombol refresh manual
                    FilledTonalIconButton(
                        onClick  = {
                            viewModel.refreshBatteryInfo()
                            countdown = BATTERY_POLL_INTERVAL_S
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "Refresh baterai",
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Battery level item dengan progress bar
            val batteryLevel = uiState.batteryLevel
            val batteryIcon  = when {
                uiState.isCharging   -> Icons.Rounded.BatteryChargingFull
                batteryLevel >= 80   -> Icons.Rounded.BatteryFull
                batteryLevel >= 50   -> Icons.Rounded.Battery5Bar
                batteryLevel >= 20   -> Icons.Rounded.Battery3Bar
                batteryLevel >= 0    -> Icons.Rounded.Battery1Bar
                else                 -> Icons.Rounded.BatteryUnknown
            }
            val batteryColor = when {
                uiState.isCharging -> MaterialTheme.colorScheme.tertiary
                batteryLevel >= 50 -> MaterialTheme.colorScheme.primary
                batteryLevel >= 20 -> MaterialTheme.colorScheme.secondary
                batteryLevel >= 0  -> MaterialTheme.colorScheme.error
                else               -> MaterialTheme.colorScheme.onSurface
            }

            // Card battery dengan progress bar
            Card(
                shape     = RoundedCornerShape(14.dp),
                colors    = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape    = RoundedCornerShape(10.dp),
                            color    = batteryColor.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    batteryIcon, null,
                                    tint     = batteryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Level Baterai", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(
                                text     = if (batteryLevel >= 0) "$batteryLevel%" else "Memuat...",
                                fontSize = 12.sp,
                                color    = batteryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Badge status
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = batteryColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text       = when (uiState.batteryStatus) {
                                    "Charging"     -> "⚡ Charging"
                                    "Full"         -> "✓ Penuh"
                                    "Discharging"  -> "Discharging"
                                    "Not Charging" -> "Tidak Dicas"
                                    else           -> uiState.batteryStatus
                                },
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = batteryColor,
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Progress bar level baterai
                    if (batteryLevel >= 0) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress    = { batteryLevel / 100f },
                            modifier    = Modifier.fillMaxWidth().height(6.dp),
                            color       = batteryColor,
                            trackColor  = batteryColor.copy(alpha = 0.15f),
                            strokeCap   = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }
            }

            // Status charging
            SettingsItem(
                title    = "Status Pengisian Daya",
                subtitle = when (uiState.batteryStatus) {
                    "Charging"     -> "Sedang diisi daya ⚡"
                    "Discharging"  -> "Tidak diisi daya"
                    "Full"         -> "Baterai penuh ✓"
                    "Not Charging" -> "Terhubung tapi tidak diisi"
                    else           -> uiState.batteryStatus
                },
                icon = if (uiState.isCharging) Icons.Rounded.Power else Icons.Rounded.PowerOff
            )

            Spacer(Modifier.height(8.dp))

            // ── Aplikasi ───────────────────────────────────────────────
            SettingsSectionTitle("Aplikasi")

            SettingsItem(
                title    = "Versi Aplikasi",
                subtitle = uiState.appVersion.ifEmpty { "1.0.0" },
                icon     = Icons.Rounded.Info
            )
            SettingsItem(
                title    = "Pengembang",
                subtitle = "Muhammad Fajri Firdaus",
                icon     = Icons.Rounded.Person
            )
            SettingsItem(
                title    = "NIM",
                subtitle = "123140050",
                icon     = Icons.Rounded.Numbers
            )
            SettingsItem(
                title    = "Mata Kuliah",
                subtitle = "IF25-22017 Pengembangan Aplikasi Mobile",
                icon     = Icons.Rounded.School
            )

            Spacer(Modifier.height(8.dp))

            // ── Navigasi ───────────────────────────────────────────────
            SettingsSectionTitle("Navigasi")

            SettingsItem(
                title    = "Bottom Navigation",
                subtitle = "3 tabs: News, Notes, Favorites, Profile",
                icon     = Icons.Rounded.ViewQuilt
            )
            SettingsItem(
                title    = "Navigation Drawer",
                subtitle = "Geser dari kiri atau tap ikon menu",
                icon     = Icons.Rounded.Menu
            )
            SettingsItem(
                title    = "Back Navigation",
                subtitle = "Tombol back di setiap screen",
                icon     = Icons.Rounded.ArrowBack
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text       = title,
        fontSize   = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color      = MaterialTheme.colorScheme.primary,
        modifier   = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
    )
}

@Composable
private fun SettingsItem(
    title   : String,
    subtitle: String,
    icon    : ImageVector,
    trailing: @Composable (() -> Unit)? = null
) {
    Card(
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape    = RoundedCornerShape(10.dp),
                color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            trailing?.invoke()
        }
    }
}
