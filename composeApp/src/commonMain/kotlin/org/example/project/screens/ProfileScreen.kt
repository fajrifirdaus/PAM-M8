package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.theme.AzureBlue
import org.example.project.ui.theme.AzureDark
import org.example.project.ui.theme.AzureLight
import org.example.project.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.painterResource
import pamtugas8.composeapp.generated.resources.Res
import pamtugas8.composeapp.generated.resources.foto_profil

// ═══════════════════════════════════════════════════
// PROFILE SCREEN — Tab ketiga
// Diadaptasi dari Tugas Minggu 4 (State Management & MVVM)
// Foto profil dari composeResources/drawable/foto_profil.jpg
// ═══════════════════════════════════════════════════

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    isDarkMode: Boolean
) {
    val uiState by profileViewModel.uiState.collectAsState()

    var editName  by remember(uiState.name)     { mutableStateOf(uiState.name) }
    var editNim   by remember(uiState.nim)      { mutableStateOf(uiState.nim) }
    var editProdi by remember(uiState.prodi)    { mutableStateOf(uiState.prodi) }
    var editBio   by remember(uiState.bio)      { mutableStateOf(uiState.bio) }
    var editEmail by remember(uiState.email)    { mutableStateOf(uiState.email) }
    var editPhone by remember(uiState.phone)    { mutableStateOf(uiState.phone) }
    var editLoc   by remember(uiState.location) { mutableStateOf(uiState.location) }

    val accentColor   = if (isDarkMode) AzureLight else AzureBlue
    val mainTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor  = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val cardColor     = if (isDarkMode) Color(0xFF1E293B) else Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Gradient header banner ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Brush.verticalGradient(listOf(AzureDark, AzureBlue)))
            )

            // ── Profile card (overlap dengan banner) ──
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-56).dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ── Foto Profil dari composeResources ──
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(4.dp, accentColor.copy(alpha = 0.4f)),
                        modifier = Modifier.size(110.dp),
                        color = Color.Transparent
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.foto_profil),
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        uiState.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = mainTextColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "NIM ${uiState.nim} • ${uiState.prodi}",
                        fontSize = 13.sp,
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        uiState.bio,
                        fontSize = 13.sp,
                        color = subTextColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            // Kompensasi offset card
            Spacer(Modifier.height((-40).dp))

            // ── Tombol aksi ──
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { profileViewModel.toggleContact() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Rounded.ContactPage, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Kontak", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { profileViewModel.toggleEditMode() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, accentColor)
                ) {
                    Icon(Icons.Rounded.Edit, null, Modifier.size(16.dp), tint = accentColor)
                    Spacer(Modifier.width(6.dp))
                    Text("Edit", color = accentColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Seksi Kontak ──
            AnimatedVisibility(visible = uiState.showContact && !uiState.isEditMode) {
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Detail Kontak",
                            fontWeight = FontWeight.Bold,
                            color = mainTextColor,
                            fontSize = 16.sp
                        )
                        ContactRow(Icons.Rounded.Email,      "Email",    uiState.email,    accentColor, mainTextColor)
                        ContactRow(Icons.Rounded.Phone,      "WhatsApp", uiState.phone,    accentColor, mainTextColor)
                        ContactRow(Icons.Rounded.LocationOn, "Lokasi",   uiState.location, accentColor, mainTextColor)
                    }
                }
            }

            // ── Seksi Edit ──
            AnimatedVisibility(visible = uiState.isEditMode) {
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Update Profil", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = mainTextColor)
                        listOf(
                            Triple(editName,  "Nama")            { v: String -> editName  = v },
                            Triple(editNim,   "NIM")             { v: String -> editNim   = v },
                            Triple(editProdi, "Program Studi")   { v: String -> editProdi = v },
                            Triple(editBio,   "Bio")             { v: String -> editBio   = v },
                            Triple(editEmail, "Email")           { v: String -> editEmail = v },
                            Triple(editPhone, "WhatsApp")        { v: String -> editPhone = v },
                            Triple(editLoc,   "Lokasi")          { v: String -> editLoc   = v }
                        ).forEach { (value, label, onValueChange) ->
                            OutlinedTextField(
                                value = value,
                                onValueChange = onValueChange,
                                label = { Text(label) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Button(
                            onClick = {
                                profileViewModel.saveChanges(
                                    editName, editNim, editProdi,
                                    editBio, editEmail, editPhone, editLoc
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Footer ──
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 60.dp),
                thickness = 0.5.dp,
                color = subTextColor.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "© 2026 Notes App PAM — Fajri Firdaus • 050",
                fontSize = 11.sp,
                color = subTextColor.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ── Snackbar sukses ──
        AnimatedVisibility(
            visible = uiState.showSuccess,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        ) {
            Surface(
                color = Color(0xFF0EA5E9),
                shape = RoundedCornerShape(50.dp),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Profil Berhasil Diperbarui", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    textColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = accent.copy(alpha = 0.12f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
        }
    }
}
