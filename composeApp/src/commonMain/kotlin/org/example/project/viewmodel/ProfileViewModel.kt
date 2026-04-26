package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val name: String = "Muhammad Fajri Firdaus",
    val nim: String = "123140050",
    val prodi: String = "Informatika",
    val bio: String = "Informatics Engineering Student at ITERA. Passionate about Mobile Development and AI Integration.",
    val email: String = "muhammad.123140050@student.itera.ac.id",
    val phone: String = "+62 899-3368-339",
    val location: String = "Palembang, Indonesia",
    val isEditMode: Boolean = false,
    val showContact: Boolean = false,
    val showSuccess: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun toggleContact() {
        _uiState.update { it.copy(showContact = !it.showContact) }
    }

    fun dismissSuccess() {
        _uiState.update { it.copy(showSuccess = false) }
    }

    fun saveChanges(
        name: String,
        nim: String,
        prodi: String,
        bio: String,
        email: String,
        phone: String,
        location: String
    ) {
        _uiState.update {
            it.copy(
                name = name,
                nim = nim,
                prodi = prodi,
                bio = bio,
                email = email,
                phone = phone,
                location = location,
                isEditMode = false,
                showContact = true,
                showSuccess = true
            )
        }

        // ✅ Auto-dismiss notifikasi setelah 3 detik
        viewModelScope.launch {
            delay(3000L)
            dismissSuccess()
        }
    }
}