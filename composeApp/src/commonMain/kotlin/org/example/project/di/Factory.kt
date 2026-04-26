package org.example.project.di

import app.cash.sqldelight.db.SqlDriver
import com.russhwolf.settings.ObservableSettings
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

expect class SettingsFactory {
    fun createSettings(): ObservableSettings
}

// --- KITA GANTI EXPECT MENJADI FUNGSI MURNI ---

fun getCurrentTime(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun formatShortDate(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val datetime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des")
    return "${datetime.dayOfMonth} ${months[datetime.monthNumber]} ${datetime.year}"
}

fun formatLongDate(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val datetime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    val months = listOf("", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")

    // UBAH DUA BARIS INI:
    // Hapus string kosong ("") di awal, dan ganti isoDayNumber menjadi ordinal
    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val dayName = days[datetime.dayOfWeek.ordinal]

    val hour = datetime.hour.toString().padStart(2, '0')
    val minute = datetime.minute.toString().padStart(2, '0')

    return "$dayName, ${datetime.dayOfMonth} ${months[datetime.monthNumber]} ${datetime.year} • $hour:$minute"
}