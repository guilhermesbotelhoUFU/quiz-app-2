package com.example.quiz_app_2.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_2.data.model.UserProfile
import com.example.quiz_app_2.data.repository.AuthRepository
import com.example.quiz_app_2.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Usuário não encontrado.") }
                return@launch
            }

            val userProfileFromDb = userRepository.getUser(currentUser.uid)
            if (userProfileFromDb != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userProfile = userProfileFromDb
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Perfil não encontrado no banco de dados.") }
            }
        }
    }
}