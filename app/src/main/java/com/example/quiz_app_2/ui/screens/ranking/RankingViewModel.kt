package com.example.quiz_app_2.ui.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_2.data.model.UserProfile
import com.example.quiz_app_2.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankingUiState(
    val isLoading: Boolean = true,
    val rankingList: List<UserProfile> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchRanking()
    }

    private fun fetchRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val ranking = userRepository.getTopRankedUsers()
                _uiState.update { it.copy(isLoading = false, rankingList = ranking) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}