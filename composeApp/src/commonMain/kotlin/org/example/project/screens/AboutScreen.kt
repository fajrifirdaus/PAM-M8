package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.theme.AzureBlue
import org.example.project.ui.theme.AzureDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp), // ✅ FIX
        topBar = {
            TopAppBar(
                title = { Text("Tentang Aplikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Kembali")
                    }
                },
                windowInsets = WindowInsets(0.dp), // ✅ FIX
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Brush.verticalGradient(listOf(AzureDark, AzureBlue))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.NoteAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Notes App", fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Versi 1.0.0", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tentang", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Notes App adalah aplikasi pencatatan modern yang dibangun " +
                                "menggunakan Jetpack Compose dan MVVM architecture. " +
                                "Dikembangkan sebagai tugas praktikum Pengembangan Aplikasi Mobile " +
                                "di Institut Teknologi Sumatera.",
                        fontSize = 14.sp, lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Fitur Utama", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    val features = listOf(
                        Icons.Rounded.Home to "Bottom Navigation (3 Tabs)",
                        Icons.Rounded.Menu to "Navigation Drawer",
                        Icons.Rounded.SwapHoriz to "Navigasi dengan Arguments",
                        Icons.Rounded.Add to "Tambah & Edit Catatan",
                        Icons.Rounded.Favorite to "Sistem Favorit",
                        Icons.Rounded.Search to "Pencarian Real-time",
                        Icons.Rounded.Palette to "Warna Catatan",
                        Icons.Rounded.DarkMode to "Dark Mode"
                    )
                    features.forEach { (icon, label) ->
                        Row(modifier = Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(label, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tech Stack", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    val techs = listOf(
                        "Jetpack Compose" to "UI Framework",
                        "Navigation Compose" to "Multi-Screen Navigation",
                        "ViewModel + StateFlow" to "State Management (MVVM)",
                        "Material Design 3" to "Design System",
                        "Kotlin" to "Programming Language",
                        "Gradle KTS + JDK 17" to "Build System"
                    )
                    techs.forEach { (tech, desc) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tech, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    desc, fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(52.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("MF", fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Muhammad Fajri Firdaus", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("NIM 123140050 • Informatika", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("Institut Teknologi Sumatera", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "© 2026 Tugas PAM Minggu 5 — ITERA",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}