package com.example.quiz_app_2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_2.data.model.UserProfile
import com.example.quiz_app_2.data.repository.AuthRepository
import com.example.quiz_app_2.data.repository.QuestionRepository
import com.example.quiz_app_2.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    data class Authenticated(val userProfile: UserProfile) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            syncAllData(firebaseUser.uid)
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email e senha não podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        authRepository.signIn(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authRepository.getCurrentUser()?.uid?.let { syncAllData(it) }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro desconhecido")
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email e senha não podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        authRepository.signUp(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = authRepository.getCurrentUser()
                    firebaseUser?.let {
                        viewModelScope.launch {
                            val newUserProfile = UserProfile(
                                uid = it.uid,
                                email = it.email ?: "email.nao.informado@quiz.com",
                                isAdmin = false
                            )
                            userRepository.saveUser(newUserProfile)

                            questionRepository.syncQuestionsIfNeeded()

                            _authState.value = AuthState.Authenticated(newUserProfile)
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao criar conta")
                }
            }
    }

    private fun syncAllData(uid: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                questionRepository.syncQuestionsIfNeeded()
                val remoteProfile = userRepository.getUserFromFirebase(uid)
                val localProfile = userRepository.getUserFromRoom(uid)
                var definitiveProfile: UserProfile? = null

                when {
                    remoteProfile != null && localProfile != null -> {
                        definitiveProfile = if (remoteProfile.stats.lastUpdated > localProfile.stats.lastUpdated) {
                            userRepository.saveUser(remoteProfile)
                            remoteProfile
                        } else {
                            userRepository.saveUser(localProfile)
                            localProfile
                        }
                    }
                    remoteProfile != null -> {
                        definitiveProfile = remoteProfile
                        userRepository.saveUser(remoteProfile)
                    }
                    localProfile != null -> {
                        definitiveProfile = localProfile
                        userRepository.saveUser(localProfile)
                    }
                }

                if (definitiveProfile != null) {
                    _authState.value = AuthState.Authenticated(definitiveProfile)
                } else {
                    _authState.value = AuthState.Error("Não foi possível encontrar ou sincronizar o perfil do usuário.")
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Falha na sincronização: ${e.message}")
            }
        }
    }

    fun signout() {
        authRepository.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}