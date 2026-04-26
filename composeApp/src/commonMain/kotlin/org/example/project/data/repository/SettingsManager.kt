package org.example.project.data.repository

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow

class SettingsManager(settings: ObservableSettings) {
    private val flowSettings: FlowSettings = settings.toFlowSettings()

    // Key preferences
    private val KEY_THEME = "app_dark_mode"

    // Membaca pengaturan sebagai Flow yang reaktif
    val isDarkModeFlow: Flow<Boolean> = flowSettings.getBooleanFlow(KEY_THEME, false)

    // Menyimpan pengaturan
    suspend fun toggleDarkMode(isDark: Boolean) {
        flowSettings.putBoolean(KEY_THEME, isDark)
    }
}